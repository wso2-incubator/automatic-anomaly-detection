/*
*  Copyright (c) ${date}, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package controller;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import communicator.DAScpuPublisher;
import communicator.DASmemoryPublisher;
import communicator.DASgcPublisher;
import jvmmonitor.UsageMonitor;
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


public class Controller {

    private final static Logger logger = Logger.getLogger(Controller.class);

    private final DASgcPublisher dasGCPublisher;
    private final DASmemoryPublisher dasMemoryPublisher;
    private final DAScpuPublisher dasCPUPublisher;


    /**
     * Constructor
     * Initialize DASPublisher objects
     *
     * @throws DataEndpointException
     * @throws SocketException
     * @throws UnknownHostException
     * @throws DataEndpointConfigurationException
     * @throws DataEndpointAuthenticationException
     * @throws DataEndpointAgentConfigurationException
     * @throws TransportException
     */
    public Controller() throws DataEndpointException,
            SocketException,
            UnknownHostException,
            DataEndpointConfigurationException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException {


        String hostname, username, password;
        int thrift_port;

        hostname = PropertyLoader.DAS_ADDRESS;
        username = PropertyLoader.DAS_USERNAME;
        password = PropertyLoader.DAS_PASSWORD;
        thrift_port = PropertyLoader.DAS_THRIFT_PORT;

        dasMemoryPublisher = new DASmemoryPublisher(hostname, thrift_port, username, password);
        dasCPUPublisher = new DAScpuPublisher(hostname, thrift_port, username, password);
        dasGCPublisher = new DASgcPublisher(hostname, thrift_port, username, password);

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
     */
    public void activateRemoteMonitoring() throws IOException,
            AttachNotSupportedException,
            MalformedObjectNameException,
            InterruptedException,
            MonitoringNotStartedException,
            DataEndpointException,
            AgentLoadException,
            AgentInitializationException {


        UsageMonitor usageObj = new UsageMonitor();

        while (!usageObj.stratMonitoring(PropertyLoader.TARGET_ADDRESS,
                PropertyLoader.TARGET_RMI_SERVER_PORT,
                PropertyLoader.TARGET_RMI_REGISTRY_PORT,
                PropertyLoader.TARGET_USERNAME,
                PropertyLoader.TARGET_PASSWORD)) {

            Thread.sleep(1000);
            logger.info("Start Monitoring Failed. Trying again...");
        }

        usageObj.registerGCNotifications(new GarbageCollectionLogHandler());

        executePublishing(usageObj, PropertyLoader.TARGET_ADDRESS);
    }


    /**
     * This method sends CPU and Memory usage data per second
     *
     * @param pid
     * @param appId
     * @throws IOException
     * @throws AttachNotSupportedException
     * @throws MalformedObjectNameException
     * @throws InterruptedException
     * @throws MonitoringNotStartedException
     * @throws DataEndpointException
     */
    public void activateLocalMonitoring(String pid, String appId) throws IOException,
            AttachNotSupportedException,
            MalformedObjectNameException,
            InterruptedException,
            MonitoringNotStartedException,
            DataEndpointException,
            AgentLoadException,
            AgentInitializationException {

        UsageMonitor usageObj = new UsageMonitor();

        while (!usageObj.stratMonitoring(pid)) {
            Thread.sleep(1000);
            logger.info("Start Monitoring Failed. Trying again...");
        }

        usageObj.registerGCNotifications(new GarbageCollectionLogHandler());

        executePublishing(usageObj, appId);
    }

    /**
     * Perform continues monitoring and publishing to the DAS
     *
     * @param usageMonitor - UsageMonitor obj to access JVM monitor
     * @param appId        - app id to be send to DAS
     * @throws MonitoringNotStartedException
     * @throws InterruptedException
     */
    private void executePublishing(UsageMonitor usageMonitor, String appId) throws MonitoringNotStartedException,
            InterruptedException {

        //Create thread pool
        ExecutorService executor = Executors.newFixedThreadPool(3);

        //Set AppId
        dasMemoryPublisher.setAppID(appId);
        dasCPUPublisher.setAppID(appId);
        dasGCPublisher.setAppID(appId);

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
     * Inner class to implement a GC log event listener in order to process GC log data
     */
    private class GarbageCollectionLogHandler implements GarbageCollectionListener {

        public void processGClogs(LinkedList<GarbageCollectionLog> gcLogList) {
            try {
                dasGCPublisher.publishGCData(gcLogList);
            } catch (DataEndpointAuthenticationException e) {
                e.printStackTrace();
            } catch (DataEndpointAgentConfigurationException e) {
                e.printStackTrace();
            } catch (DataEndpointException e) {
                e.printStackTrace();
            } catch (DataEndpointConfigurationException e) {
                e.printStackTrace();
            } catch (TransportException e) {
                e.printStackTrace();
            }

        }
    }


}
