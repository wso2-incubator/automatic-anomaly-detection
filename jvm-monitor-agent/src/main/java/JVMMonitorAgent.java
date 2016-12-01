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

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class to start JVMMonitor agent
 * Perform the mode checks
 */
public class JVMMonitorAgent {

    private final static Logger logger = Logger.getLogger(JVMMonitorAgent.class);

    private GCPublisher dasGCPublisher;
    private MemoryPublisher dasMemoryPublisher;
    private CPUPublisher dasCPUPublisher;

    /**
     * Constructor
     * Initialize DASPublisher objects
     */
    private JVMMonitorAgent() throws PublisherInitializationException {

        try {
            dasMemoryPublisher = new MemoryPublisher(PropertyLoader.dasAddress, PropertyLoader.dasThriftPort, PropertyLoader.dasUsername, PropertyLoader.dasPassword);
            dasCPUPublisher = new CPUPublisher(PropertyLoader.dasAddress, PropertyLoader.dasThriftPort, PropertyLoader.dasUsername, PropertyLoader.dasPassword);
            dasGCPublisher = new GCPublisher(PropertyLoader.dasAddress, PropertyLoader.dasThriftPort, PropertyLoader.dasUsername, PropertyLoader.dasPassword);
        } catch (SocketException | UnknownHostException | DataEndpointException | TransportException | DataEndpointAuthenticationException | DataEndpointAgentConfigurationException | DataEndpointConfigurationException e) {
            throw new PublisherInitializationException(e.getMessage(), e);
        }
    }

    public static void main(String[] args) throws AgentLoadException,
            AgentInitializationException {

        try {
            PropertyLoader.loadProperties();
            logger.info("Properties loaded successfully");

            JVMMonitorAgent jvmMonitor = new JVMMonitorAgent();
            jvmMonitor.runAgent();

        } catch (PropertyCannotBeLoadedException | PublisherInitializationException | MonitorAgentInitializationFailed | UnknownMonitorAgentTypeException | AccessingUsageStatisticFailedException e) {
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * Start monitoring of JVMs
     * Select between remote monitoring and local monitoring according to the configurations
     */
    private void runAgent() throws MonitorAgentInitializationFailed, UnknownMonitorAgentTypeException, AccessingUsageStatisticFailedException {
        UsageMonitorAgent usageMonitorAgent = UsageMonitorAgentFatory.getUsageMonitor(PropertyLoader.mode);
        String targetedApplicationId = usageMonitorAgent.getTargetedApplicationId();


        ExecutorService executor = Executors.newFixedThreadPool(3);
        UsageStatistic usageStatistic;
        while (true) {
            usageStatistic = usageMonitorAgent.getUsageStatistic();

            //            //Set UsageMonitorLog
//            dasMemoryPublisher.setUsageLogObj(usageLogObj);
//            dasCPUPublisher.setUsageLogObj(usageLogObj);
//
//            executor.execute(dasMemoryPublisher);
//            executor.execute(dasCPUPublisher);
//
//            Thread.sleep(1000);
        }

    }
}
