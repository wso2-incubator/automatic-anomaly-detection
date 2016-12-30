/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import communicator.CPUPublisher;
import communicator.DASConfiguration;
import communicator.GarbageCollectionPublisher;
import communicator.MemoryPublisher;
import exceptions.PropertyCannotBeLoadedException;
import exceptions.PublisherInitializationException;
import jvmmonitor.UsageMonitorAgent;
import jvmmonitor.UsageMonitorAgentFactory;
import jvmmonitor.exceptions.AccessingUsageStatisticFailedException;
import jvmmonitor.exceptions.MonitorAgentInitializationFailed;
import jvmmonitor.exceptions.UnknownMonitorAgentTypeException;
import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import util.JmaProperties;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Class to start JVMMonitor agent
 * Perform the mode checks
 */
public class JVMMonitorAgent {

    private final static Logger logger = Logger.getLogger(JVMMonitorAgent.class);

    // DAS publisher
    private GarbageCollectionPublisher dasGCPublisher;
    private MemoryPublisher dasMemoryPublisher;
    private CPUPublisher dasCPUPublisher;

    private UsageMonitorAgent usageMonitorAgent;
    private String targetedApplicationId;
    
    private final ScheduledExecutorService scheduler;

    /**
     * Constructor
     * <p>
     * Initialize DASConfiguration
     * Initialize DAS publisher
     * Select between remote monitoring and local monitoring according to the configurations
     */
    private JVMMonitorAgent() throws MonitorAgentInitializationFailed, UnknownMonitorAgentTypeException,
            PublisherInitializationException, PropertyCannotBeLoadedException {

        JmaProperties jmaProperties = new JmaProperties();

        DASConfiguration dasConfiguration = new DASConfiguration(jmaProperties.getDasAddress(),
                jmaProperties.getDasThriftPort(), jmaProperties.getDasSecurePort(), jmaProperties.getDasUsername(),
                jmaProperties.getDasPassword(), jmaProperties.getDataAgentConfPath(), jmaProperties.getTrustStorePath(),
                jmaProperties.getTrustStorePassword());

        try {

            dasMemoryPublisher = new MemoryPublisher(dasConfiguration);
            dasCPUPublisher = new CPUPublisher(dasConfiguration);
            dasGCPublisher = new GarbageCollectionPublisher(dasConfiguration);

        } catch (DataEndpointException | TransportException | DataEndpointAuthenticationException
                | DataEndpointAgentConfigurationException | DataEndpointConfigurationException e) {
            throw new PublisherInitializationException(e.getMessage(), e);
        }

        // initialize usage monitor agent according to the mode in jma.properties
        usageMonitorAgent = UsageMonitorAgentFactory.getUsageMonitorAgent(jmaProperties);
        // get generated targeted app_id
        targetedApplicationId = usageMonitorAgent.getTargetedApplicationId();
        // number of threads in the pool = 4
        scheduler = Executors.newScheduledThreadPool(4);

    }

    public static void main(String[] args) {
        try {
            JVMMonitorAgent jvmMonitor = new JVMMonitorAgent();

            jvmMonitor.startGarbageCollectionScheduler(100);
            jvmMonitor.startMemoryScheduler(1000);
            jvmMonitor.startCpuScheduler(1000);

        } catch (PropertyCannotBeLoadedException | PublisherInitializationException | MonitorAgentInitializationFailed
                | UnknownMonitorAgentTypeException e) {
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * To schedule the garbage collection task
     *
     * @param period
     */
    private void startGarbageCollectionScheduler(long period) {
        scheduler.scheduleAtFixedRate(new GarbageCollectionTask(), 0, period, MILLISECONDS);
    }

    /**
     * To schedule the memory task
     *
     * @param period
     */
    private void startMemoryScheduler(long period) {
        scheduler.scheduleAtFixedRate(new MemoryTask(), 0, period, MILLISECONDS);
    }

    /**
     * To schedule the CPU task
     *
     * @param period
     */
    private void startCpuScheduler(long period) {
        scheduler.scheduleAtFixedRate(new CpuTask(), 0, period, MILLISECONDS);
    }

    /**
     * Get garbage collection statistic and publish garbage collection event
     */
    private class GarbageCollectionTask implements Runnable {

        @Override
        public void run() {

            try {
                // publish garbage collection statistic
                dasGCPublisher.publishEvents(usageMonitorAgent.getGarbageCollectionStatistics(),
                        targetedApplicationId, new Date().getTime());

            } catch (AccessingUsageStatisticFailedException e) {
                logger.error(e.getMessage(), e);
            }

        }
    }

    /**
     * Get memory statistic and publish memory event
     */
    private class MemoryTask implements Runnable {

        @Override
        public void run() {

            // get time stamp when publishing the data
            long timeStamp = new Date().getTime();

            try {
                // publish memory statistic
                dasMemoryPublisher.publishEvents(usageMonitorAgent.getMemoryStatistics(), targetedApplicationId,
                        timeStamp);

            } catch (AccessingUsageStatisticFailedException e) {
                logger.error(e.getMessage(), e);
            }

        }
    }

    /**
     * Get CPU statistic and publish CPU event
     */
    private class CpuTask implements Runnable {

        @Override
        public void run() {

            // get time stamp when publishing the data
            long timeStamp = new Date().getTime();

            try {
                // publish CPU statistic
                dasCPUPublisher.publishEvents(usageMonitorAgent.getCPUStatistics(), targetedApplicationId, timeStamp);

            } catch (AccessingUsageStatisticFailedException e) {
                logger.error(e.getMessage(), e);
            }

        }
    }

}
