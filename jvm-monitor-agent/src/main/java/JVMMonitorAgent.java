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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class to start JVMMonitor agent
 * Perform the mode checks
 */
public class JVMMonitorAgent {

    private final static Logger logger = Logger.getLogger(JVMMonitorAgent.class);

    // DAS Publisher
    private GarbageCollectionPublisher dasGCPublisher;
    private MemoryPublisher dasMemoryPublisher;
    private CPUPublisher dasCPUPublisher;

    private UsageMonitorAgent usageMonitorAgent;
    private String targetedApplicationId;


    private ExecutorService executor;

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

        // Initialize usage monitor agent according to the mode in jma.properties
        usageMonitorAgent = UsageMonitorAgentFactory.getUsageMonitorAgent(jmaProperties);
        // Get generated targeted app_id
        targetedApplicationId = usageMonitorAgent.getTargetedApplicationId();

        executor = Executors.newFixedThreadPool(4);

    }

    public static void main(String[] args) {
        try {
            JVMMonitorAgent jvmMonitor = new JVMMonitorAgent();

            jvmMonitor.startGarbageCollectionTimer(100);
            jvmMonitor.startMemoryCpuTimer(1000);

        } catch (PropertyCannotBeLoadedException | PublisherInitializationException | MonitorAgentInitializationFailed
                | UnknownMonitorAgentTypeException e) {
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * To schedule the Garbage collection event
     *
     * @param period
     */
    private void startGarbageCollectionTimer(long period) {
        Timer timer = new Timer();
        timer.schedule(new GarbageCollectionTaskScheduler(), 0, period);
    }

    /**
     * To schedule the Memory and CPU event
     *
     * @param period
     */
    private void startMemoryCpuTimer(long period) {
        Timer timer = new Timer();
        timer.schedule(new MemoryCpuTaskScheduler(), 0, period);
    }

    /**
     * Inner class
     * Implementation of TimerTask
     */
    private class GarbageCollectionTaskScheduler extends TimerTask {

        @Override
        public void run() {

            try {
                // Set garbage collection statistic to publish
                dasGCPublisher.setGarbageCollectionStatistic(usageMonitorAgent.getGarbageCollectionStatistics(),
                        targetedApplicationId, new Date().getTime());
                executor.execute(dasGCPublisher);

            } catch (AccessingUsageStatisticFailedException e) {
                logger.error(e.getMessage(), e);
            }

        }
    }

    /**
     * Inner class
     * Implementation of TimerTask
     */
    private class MemoryCpuTaskScheduler extends TimerTask {

        @Override
        public void run() {

            // get time stamp when publishing the data (should be unique to all publishers)
            long timeStamp = new Date().getTime();

            try {
                // Set Memory statistic to publish
                dasMemoryPublisher.setMemoryStatistic(usageMonitorAgent.getMemoryStatistics(), targetedApplicationId,
                        timeStamp);
                executor.execute(dasMemoryPublisher);

                // Set CPU statistic to publish
                dasCPUPublisher.setCPUStatistic(usageMonitorAgent.getCPUStatistics(), targetedApplicationId, timeStamp);
                executor.execute(dasCPUPublisher);

            } catch (AccessingUsageStatisticFailedException e) {
                logger.error(e.getMessage(), e);
            }

        }
    }

}
