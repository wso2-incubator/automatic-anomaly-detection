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

import javax.management.MalformedObjectNameException;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Controller implements GarbageCollectionListener {

    final static Logger logger = Logger.getLogger(Controller.class);

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

        dasMemoryPublisher = new DASmemoryPublisher(7611, 9611, "admin", "admin");
        dasCPUPublisher = new DAScpuPublisher(7611, 9611, "admin", "admin");
        dasGCPublisher = new DASgcPublisher(7611, 9611, "admin", "admin");

    }

    /**
     * This method sends CPU and Memory usage data per second
     *
     * @param pid
     * @param controllerObj
     * @throws IOException
     * @throws AttachNotSupportedException
     * @throws MalformedObjectNameException
     * @throws InterruptedException
     * @throws MonitoringNotStartedException
     * @throws DataEndpointException
     */
    public void sendUsageData(String pid, String appID, Controller controllerObj, String[] credential) throws IOException,
            AttachNotSupportedException,
            MalformedObjectNameException,
            InterruptedException,
            MonitoringNotStartedException,
            DataEndpointException {

        UsageMonitor usageObj = new UsageMonitor(pid);
        if (credential != null){
            usageObj.setCredential(credential);
        }

        while (!usageObj.stratMonitoring()){
            Thread.sleep(1000);
            logger.info("Start Monitoring Failed. Trying again...");
        }

        usageObj.registerGCNotifications(controllerObj);

        //Create thread pool
        ExecutorService executor = Executors.newFixedThreadPool(3);

        Runnable memory = dasMemoryPublisher;
        Runnable cpu = dasCPUPublisher;

        //Set AppId
        dasMemoryPublisher.setAppID(appID);
        dasCPUPublisher.setAppID(appID);
        dasGCPublisher.setAppID(appID);

        while (true) {

            UsageMonitorLog usageLogObj = usageObj.getUsageLog();

            //Set UsageMonitorLog
            dasMemoryPublisher.setUsageLogObj(usageLogObj);
            dasCPUPublisher.setUsageLogObj(usageLogObj);

            executor.execute(memory);
            executor.execute(cpu);

            Thread.sleep(1000);

        }

//        executor.shutdown();
//        while (!executor.isTerminated()) {
//        }
//
//        dasMemoryPublisher.shutdownDataPublisher();
//        dasCPUPublisher.shutdownDataPublisher();
//        dasGCPublisher.shutdownDataPublisher();

    }

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
