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
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import communicator.DAScpuPublisher;
import communicator.DASgcPublisher;
import communicator.DASmemoryPublisher;
import exceptions.PropertyCannotBeLoadedException;
import exceptions.StartMonitoringFailedException;
import exceptions.StartPublishingFailedException;
import exceptions.UnsupportedMonitoringModeException;
import jvmmonitor.UsageMonitorAgent;
import jvmmonitor.exceptions.MonitoringNotStartedException;
import jvmmonitor.model.GarbageCollectionLog;
import jvmmonitor.model.UsageMonitorLog;
import jvmmonitor.util.GarbageCollectionListener;
import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import util.PropertyLoader;

import javax.management.MalformedObjectNameException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class JVMMonitorAgent {

    private final static Logger logger = Logger.getLogger(JVMMonitorAgent.class);

    private DASgcPublisher dasGCPublisher;
    private DASmemoryPublisher dasMemoryPublisher;
    private DAScpuPublisher dasCPUPublisher;

    /**
     * Constructor
     * Initialize DASPublisher objects
     */
    private JVMMonitorAgent() throws StartPublishingFailedException {

        try {
            dasMemoryPublisher = new DASmemoryPublisher(PropertyLoader.dasAddress, PropertyLoader.dasThriftPort, PropertyLoader.dasUsername, PropertyLoader.dasPassword);
            dasCPUPublisher = new DAScpuPublisher(PropertyLoader.dasAddress, PropertyLoader.dasThriftPort, PropertyLoader.dasUsername, PropertyLoader.dasPassword);
            dasGCPublisher = new DASgcPublisher(PropertyLoader.dasAddress, PropertyLoader.dasThriftPort, PropertyLoader.dasUsername, PropertyLoader.dasPassword);
        } catch (SocketException e) {
            logger.error(e.getMessage(), e);
            throw new StartPublishingFailedException(e.getMessage(), e);
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
            throw new StartPublishingFailedException(e.getMessage(), e);
        } catch (DataEndpointAuthenticationException e) {
            logger.error(e.getMessage(), e);
            throw new StartPublishingFailedException(e.getMessage(), e);
        } catch (DataEndpointAgentConfigurationException e) {
            logger.error(e.getMessage(), e);
            throw new StartPublishingFailedException(e.getMessage(), e);
        } catch (TransportException e) {
            logger.error(e.getMessage(), e);
            throw new StartPublishingFailedException(e.getMessage(), e);
        } catch (DataEndpointException e) {
            logger.error(e.getMessage(), e);
            throw new StartPublishingFailedException(e.getMessage(), e);
        } catch (DataEndpointConfigurationException e) {
            logger.error(e.getMessage(), e);
            throw new StartPublishingFailedException(e.getMessage(), e);
        }

    }

    /**
     * Start monitoring of JVMs
     * Select between remote monitoring and local monitoring according to the configurations
     *
     * @throws StartMonitoringFailedException
     * @throws UnsupportedMonitoringModeException
     * @throws MonitoringNotStartedException
     * @throws InterruptedException
     */
    private void runMonitor() throws StartMonitoringFailedException,
            UnsupportedMonitoringModeException,
            MonitoringNotStartedException,
            InterruptedException {

        try {

            if ("jmx".equals(PropertyLoader.mode)) { //if mode == jmx, uses JMX URL to connect
                activateRemoteMonitoring();
            } else if ("pid".equals(PropertyLoader.mode)) { //monitoring using PID is enabled start monitoring using the PID
                String pid, appName;
                pid = PropertyLoader.pid;

                if (pid == null) {
                    String msg = "No pid is given";
                    logger.error(msg);
                    throw new StartMonitoringFailedException(msg);

                } else {
                    appName = getAppName(pid);
                    if (appName == null) {
                        String msg = "No running application found with given PID : " + pid;
                        logger.error(msg);
                        throw new StartMonitoringFailedException(msg);
                    } else {
                        activateLocalMonitoring(appName);
                    }
                }
            } else { // unsupported monitoring mode
                String msg = "No monitoring mode is found with :" + PropertyLoader.mode;
                logger.error(msg);
                throw new UnsupportedMonitoringModeException(msg);
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new StartMonitoringFailedException(e.getMessage(), e);
        } catch (AttachNotSupportedException e) {
            logger.error(e.getMessage(), e);
            throw new StartMonitoringFailedException(e.getMessage(), e);
        } catch (MalformedObjectNameException e) {
            logger.error(e.getMessage(), e);
            throw new StartMonitoringFailedException(e.getMessage(), e);
        } catch (DataEndpointException e) {
            logger.error(e.getMessage(), e);
            throw new StartMonitoringFailedException(e.getMessage(), e);
        } catch (AgentLoadException e) {
            logger.error(e.getMessage(), e);
            throw new StartMonitoringFailedException(e.getMessage(), e);
        } catch (AgentInitializationException e) {
            logger.error(e.getMessage(), e);
            throw new StartMonitoringFailedException(e.getMessage(), e);
        }
    }

    /**
     * Method start remote monitoring target application and publishing the usage data
     *
     * @throws IOException
     * @throws AttachNotSupportedException
     * @throws MalformedObjectNameException
     * @throws InterruptedException
     * @throws MonitoringNotStartedException
     * @throws DataEndpointException
     * @throws AgentLoadException
     * @throws AgentInitializationException
     */
    private void activateRemoteMonitoring() throws IOException,
            AttachNotSupportedException,
            MalformedObjectNameException,
            InterruptedException,
            MonitoringNotStartedException,
            DataEndpointException,
            AgentLoadException,
            AgentInitializationException {


        UsageMonitorAgent usageObj = new UsageMonitorAgent();

        //check if credentials are not required
        //check if username == "null"
        if ("null".equals(PropertyLoader.targetUsername)) {
            PropertyLoader.targetUsername = null;
            PropertyLoader.targetPassword = null;
        }

        while (!usageObj.startMonitoring(PropertyLoader.targetAddress,
                PropertyLoader.targetRmiServerPort,
                PropertyLoader.targetRmiRegistryPort,
                PropertyLoader.targetUsername,
                PropertyLoader.targetPassword)) {

            Thread.sleep(1000);
            logger.info("Start Monitoring Failed. Trying again...");
        }

        usageObj.registerGCNotifications(new GarbageCollectionLogHandler());

        executePublishing(usageObj, PropertyLoader.targetAddress);
    }

    /**
     * Method to start local monitoring target application and publishing the usage data
     *
     * @param appName - Name of targeted application
     * @throws IOException
     * @throws AttachNotSupportedException
     * @throws MalformedObjectNameException
     * @throws InterruptedException
     * @throws MonitoringNotStartedException
     * @throws DataEndpointException
     * @throws AgentLoadException
     * @throws AgentInitializationException
     */
    private void activateLocalMonitoring(String appName) throws IOException,
            AttachNotSupportedException,
            MalformedObjectNameException,
            InterruptedException,
            MonitoringNotStartedException,
            DataEndpointException,
            AgentLoadException,
            AgentInitializationException {

        UsageMonitorAgent usageObj = new UsageMonitorAgent();

        while (!usageObj.startMonitoring(PropertyLoader.pid)) {
            Thread.sleep(1000);
            logger.info("Start Monitoring Failed. Trying again...");
        }

        usageObj.registerGCNotifications(new GarbageCollectionLogHandler());

        executePublishing(usageObj, appName);
    }

    /**
     * Perform continues monitoring and publishing to the DAS
     *
     * @param usageMonitor - UsageMonitorAgent obj to access JVM monitor
     * @param appName      - Targeted application name to be send to DAS
     * @throws MonitoringNotStartedException
     * @throws InterruptedException
     */
    private void executePublishing(UsageMonitorAgent usageMonitor, String appName) throws MonitoringNotStartedException,
            InterruptedException {

        //Create thread pool
        ExecutorService executor = Executors.newFixedThreadPool(3);

        //Set AppId
        dasMemoryPublisher.setAppID(appName);
        dasCPUPublisher.setAppID(appName);
        dasGCPublisher.setAppID(appName);

        while (true) {

            UsageMonitorLog usageLogObj = usageMonitor.getUsageLog();

            //Set UsageMonitorLog
            dasMemoryPublisher.setUsageLogObj(usageLogObj);
            dasCPUPublisher.setUsageLogObj(usageLogObj);

            executor.execute(dasMemoryPublisher);
            executor.execute(dasCPUPublisher);

            Thread.sleep(1000);
        }
    }

    /**
     * Get Display name of given PID
     *
     * @param pid - Process Id of targeted application
     * @return
     */
    private String getAppName(String pid) {

        String appName = null;

        for (VirtualMachineDescriptor vmd : VirtualMachine.list()) {
            if (pid != null && pid.equals(vmd.id())) {
                appName = vmd.displayName();
                logger.info("AppName found. PID: " + vmd.id() + "\tName: " + vmd.displayName());
                break;
            }
        }
        return appName;
    }


    public static void main(String[] args) throws AgentLoadException,
            AgentInitializationException {

        try {

            PropertyLoader.loadProperties();
            logger.info("Properties loaded successfully");

            JVMMonitorAgent jvmMonitor = new JVMMonitorAgent();
            jvmMonitor.runMonitor();

        } catch (PropertyCannotBeLoadedException e) {
            logger.error(e.getMessage(), e);
        } catch (StartPublishingFailedException e) {
            logger.error(e.getMessage(), e);
        } catch (StartMonitoringFailedException e) {
            logger.error(e.getMessage(), e);
        } catch (MonitoringNotStartedException e) {
            logger.error(e.getMessage(), e);
        } catch (UnsupportedMonitoringModeException e) {
            logger.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Inner class to implement a GC log event listener in order to process GC log data
     */
    private class GarbageCollectionLogHandler implements GarbageCollectionListener {

        public void processGClogs(LinkedList<GarbageCollectionLog> gcLogList) {
            try {
                dasGCPublisher.publishGCData(gcLogList);
            } catch (DataEndpointAuthenticationException e) {
                logger.error(e.getMessage(), e);
            } catch (DataEndpointAgentConfigurationException e) {
                logger.error(e.getMessage(), e);
            } catch (DataEndpointException e) {
                logger.error(e.getMessage(), e);
            } catch (DataEndpointConfigurationException e) {
                logger.error(e.getMessage(), e);
            } catch (TransportException e) {
                logger.error(e.getMessage(), e);
            }

        }
    }
}
