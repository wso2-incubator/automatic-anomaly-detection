package controller;

import com.sun.tools.attach.AttachNotSupportedException;
import communicator.DASPublisher;
import jvmmonitor.UsageMonitor;
import jvmmonitor.exceptions.MonitoringNotStartedException;
import jvmmonitor.model.GarbageCollectionLog;
import jvmmonitor.model.UsageMonitorLog;
import jvmmonitor.util.GarbageCollectionListener;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

import javax.management.MalformedObjectNameException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;

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

public class Controller implements GarbageCollectionListener {

    private final DASPublisher dasGCPublisher;
    private final DASPublisher dasMemoryPublisher;
    private final DASPublisher dasCPUPublisher;

    private static long startTime;

    public Controller() throws DataEndpointException,
            SocketException,
            UnknownHostException,
            DataEndpointConfigurationException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException {

        dasMemoryPublisher = new DASPublisher(7611, 9611, "admin", "admin");
        dasCPUPublisher = new DASPublisher(7611, 9611, "admin", "admin");
        dasGCPublisher = new DASPublisher(7611, 9611, "admin", "admin");

    }

    public void sendUsageData(String pid) throws IOException,
            AttachNotSupportedException,
            MalformedObjectNameException,
            InterruptedException,
            MonitoringNotStartedException,
            DataEndpointException {

        final UsageMonitor usageObj = new UsageMonitor(pid);
        usageObj.registerGCNotifications(this);
        usageObj.stratMonitoring();

        //final DASPublisher dasMemoryPublisher = new DASPublisher(7611, 9611, "admin", "admin");
        //final DASPublisher dasCPUPublisher = new DASPublisher(7611, 9611, "admin", "admin");
        //dasGCPublisher = new DASPublisher(7611, 9611, "admin", "admin");

        startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < 60000) {

            final UsageMonitorLog usageLogObj = usageObj.getUsageLog();

            Thread memoryThread = new Thread() {
                public void run() {
                    try {
                        dasMemoryPublisher.publishMemoryData(usageLogObj.getDate(), usageLogObj.getMemoryUsageLog());
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
            };
            memoryThread.start();

            Thread cpuThread = new Thread() {
                public void run() {
                    try {
                        dasCPUPublisher.publishCPUData(usageLogObj.getDate(), usageLogObj.getCpuLoadLog());
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
            };
            cpuThread.start();

            /*
            Thread gcThread = new Thread() {
                public void run() {
                    try {
                        dasGCPublisher.publishGCData((LinkedList<GarbageCollectionLog>) usageLogObj.getGarbageCollectionLog());
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
            };
            gcThread.start();
            */

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        dasMemoryPublisher.shutdownDataPublisher();
        dasCPUPublisher.shutdownDataPublisher();
        dasGCPublisher.shutdownDataPublisher();

    }


    public void processGClogs(final LinkedList<GarbageCollectionLog> gcLogList) {

        Thread gcThread = new Thread() {
            public void run() {
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
        };

    }
}
