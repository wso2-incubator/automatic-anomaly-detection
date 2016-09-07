package controller;

import com.sun.tools.attach.AttachNotSupportedException;
import communicator.DASPublisher;
import jvmmonitor.UsageMonitor;
import jvmmonitor.exceptions.MonitoringNotStartedException;
import jvmmonitor.model.GarbageCollectionLog;
import jvmmonitor.model.UsageMonitorLog;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

import javax.management.MalformedObjectNameException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

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

public class Controller {

    private static long startTime;

    public void sendUsageData(String pid) throws IOException,
            AttachNotSupportedException,
            MalformedObjectNameException,
            InterruptedException,
            MonitoringNotStartedException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointException,
            DataEndpointConfigurationException {

        UsageMonitor usageObj = new UsageMonitor(pid);
        usageObj.stratMonitoring();
        UsageMonitorLog usageLogObj;

        DASPublisher dasMemoryPublisher = new DASPublisher(7611, 9611, "admin", "admin");
        DASPublisher dasCPUPublisher = new DASPublisher(7611, 9611, "admin", "admin");
        DASPublisher dasGCPublisher = new DASPublisher(7611, 9611, "admin", "admin");

        startTime = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < 60000) {

            usageLogObj = usageObj.getUsageLog();

            dasMemoryPublisher.publishMemoryData(usageLogObj.getDate(), usageLogObj.getMemoryUsageLog());
            dasCPUPublisher.publishCPUData(usageLogObj.getDate(), usageLogObj.getCpuLoadLog());
            dasGCPublisher.publishGCData((LinkedList<GarbageCollectionLog>) usageLogObj.getGarbageCollectionLog());

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

}
