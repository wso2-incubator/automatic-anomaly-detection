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

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import communicator.CPUPublisher;
import communicator.DASConfigurations;
import communicator.GCPublisher;
import communicator.MemoryPublisher;
import exceptions.PropertyCannotBeLoadedException;
import exceptions.PublisherInitializationException;
import jvmmonitor.UsageMonitorAgent;
import jvmmonitor.UsageMonitorAgentFatory;
import jvmmonitor.exceptions.AccessingUsageStatisticFailedException;
import jvmmonitor.exceptions.MonitorAgentInitializationFailed;
import jvmmonitor.exceptions.UnknownMonitorAgentTypeException;
import jvmmonitor.models.UsageStatistic;
import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import util.PropertyLoader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class to start JVMMonitor agent
 * Perform the mode checks
 */
public class JVMMonitorAgent {

    private final static Logger logger = Logger.getLogger(JVMMonitorAgent.class);

    public static void main(String[] args) throws AgentLoadException, AgentInitializationException {

        try {
            PropertyLoader.loadProperties();
            logger.info("Properties loaded successfully");

            JVMMonitorAgent jvmMonitor = new JVMMonitorAgent();
            jvmMonitor.runAgent();

        } catch (PropertyCannotBeLoadedException | PublisherInitializationException | MonitorAgentInitializationFailed
                | UnknownMonitorAgentTypeException | AccessingUsageStatisticFailedException e) {
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * Start monitoring of JVMs
     * Select between remote monitoring and local monitoring according to the configurations
     */
    private void runAgent() throws MonitorAgentInitializationFailed, UnknownMonitorAgentTypeException,
            AccessingUsageStatisticFailedException, PublisherInitializationException {

        GCPublisher dasGCPublisher;
        MemoryPublisher dasMemoryPublisher;
        CPUPublisher dasCPUPublisher;

        DASConfigurations dasConfigurations = new DASConfigurations(PropertyLoader.dasAddress,
                PropertyLoader.dasThriftPort, PropertyLoader.dasSecurePort, PropertyLoader.dasUsername,
                PropertyLoader.dasPassword, PropertyLoader.dataAgentConfPath, PropertyLoader.trustStorePath,
                PropertyLoader.trustStorePassword);

        try {

            dasMemoryPublisher = new MemoryPublisher(dasConfigurations);
            dasCPUPublisher = new CPUPublisher(dasConfigurations);
            dasGCPublisher = new GCPublisher(dasConfigurations);

        } catch (DataEndpointException | TransportException | DataEndpointAuthenticationException
                | DataEndpointAgentConfigurationException | DataEndpointConfigurationException e) {
            throw new PublisherInitializationException(e.getMessage(), e);
        }

        UsageMonitorAgent usageMonitorAgent = UsageMonitorAgentFatory.getUsageMonitor(PropertyLoader.mode);
        String targetedApplicationId = usageMonitorAgent.getTargetedApplicationId();

        ExecutorService executor = Executors.newFixedThreadPool(4);
        UsageStatistic usageStatistic;
        int counter = 1;

        while (true) {
            usageStatistic = usageMonitorAgent.getUsageStatistic();

            //Set data to publisher
            dasGCPublisher.setGarbageCollectionStatistic(usageStatistic.getGarbageCollectionStatistics()
                    , targetedApplicationId, usageStatistic.getTimeStamp());
            dasMemoryPublisher.setMemoryStatistic(usageStatistic.getMemoryStatistics(), targetedApplicationId
                    , usageStatistic.getTimeStamp());
            dasCPUPublisher.setCPUStatistic(usageStatistic.getCpuStatistics(), targetedApplicationId
                    , usageStatistic.getTimeStamp());

            executor.execute(dasGCPublisher);

            if (counter == 10) {
                executor.execute(dasMemoryPublisher);
                executor.execute(dasCPUPublisher);
                counter = 1;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            counter++;
        }
    }
}
