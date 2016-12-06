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
import communicator.DASConfigurations;
import communicator.GarbageCollectionPublisher;
import communicator.MemoryPublisher;
import exceptions.PropertyCannotBeLoadedException;
import exceptions.PublisherInitializationException;
import jvmmonitor.UsageMonitorAgent;
import jvmmonitor.UsageMonitorAgentFatory;
import jvmmonitor.exceptions.AccessingUsageStatisticFailedException;
import jvmmonitor.exceptions.MonitorAgentInitializationFailed;
import jvmmonitor.exceptions.UnknownMonitorAgentTypeException;
import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import util.PropertyLoader;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class to start JVMMonitor agent
 * Perform the mode checks
 */
public class JVMMonitorAgent extends TimerTask {

    private final static Logger logger = Logger.getLogger(JVMMonitorAgent.class);

    //DAS Publisher
    private GarbageCollectionPublisher dasGCPublisher;
    private MemoryPublisher dasMemoryPublisher;
    private CPUPublisher dasCPUPublisher;

    private UsageMonitorAgent usageMonitorAgent;
    private String targetedApplicationId;
    private ExecutorService executor;
    private int counter = 1;

    /**
     * Constructor
     * <p>
     * Initialize DASConfigurations
     * Initialize DAS publisher
     * Select between remote monitoring and local monitoring according to the configurations
     */
    private JVMMonitorAgent() throws MonitorAgentInitializationFailed, UnknownMonitorAgentTypeException,
            PublisherInitializationException {

        DASConfigurations dasConfigurations = new DASConfigurations(PropertyLoader.dasAddress,
                PropertyLoader.dasThriftPort, PropertyLoader.dasSecurePort, PropertyLoader.dasUsername,
                PropertyLoader.dasPassword, PropertyLoader.dataAgentConfPath, PropertyLoader.trustStorePath,
                PropertyLoader.trustStorePassword);

        try {

            dasMemoryPublisher = new MemoryPublisher(dasConfigurations);
            dasCPUPublisher = new CPUPublisher(dasConfigurations);
            dasGCPublisher = new GarbageCollectionPublisher(dasConfigurations);

        } catch (DataEndpointException | TransportException | DataEndpointAuthenticationException
                | DataEndpointAgentConfigurationException | DataEndpointConfigurationException e) {
            throw new PublisherInitializationException(e.getMessage(), e);
        }

        //Initialize usage monitor agent according to the mode in jma.properties
        usageMonitorAgent = UsageMonitorAgentFatory.getUsageMonitor(PropertyLoader.mode);
        //Get generated targeted app_id
        targetedApplicationId = usageMonitorAgent.getTargetedApplicationId();

        executor = Executors.newFixedThreadPool(4);

    }

    @Override
    public void run() {

        //get time stamp when publishing the data (should be unique to all publishers)
        long timeStamp = new Date().getTime();

        try {
            //Set garbage collection statistic to publish
            dasGCPublisher.setGarbageCollectionStatistic(usageMonitorAgent.getGarbageCollectionStatistics()
                    , targetedApplicationId, timeStamp);
            executor.execute(dasGCPublisher);

        } catch (AccessingUsageStatisticFailedException e) {
            logger.error(e.getMessage(), e);
        }

        if (counter == 10) {
            try {
                //Set Memory statistic to publish
                dasMemoryPublisher.setMemoryStatistic(usageMonitorAgent.getMemoryStatistics(), targetedApplicationId
                        , timeStamp);
                executor.execute(dasMemoryPublisher);

                //Set CPU statistic to publish
                dasCPUPublisher.setCPUStatistic(usageMonitorAgent.getCPUStatistics(), targetedApplicationId
                        , timeStamp);
                executor.execute(dasCPUPublisher);

            } catch (AccessingUsageStatisticFailedException e) {
                logger.error(e.getMessage(), e);
            }

            counter = 0;
        }

        counter++;
    }


    public static void main(String[] args) {

        try {
            PropertyLoader.loadProperties();
            logger.info("Properties loaded successfully");

            JVMMonitorAgent jvmMonitor = new JVMMonitorAgent();

            Timer timer = new Timer();
            timer.schedule(jvmMonitor, 0, 100);

        } catch (PropertyCannotBeLoadedException | PublisherInitializationException | MonitorAgentInitializationFailed
                | UnknownMonitorAgentTypeException e) {
            logger.error(e.getMessage(), e);
        }

    }

}
