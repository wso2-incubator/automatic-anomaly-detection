package jvmmonitor;/*
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

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import jvmmonitor.exceptions.MonitoringNotStartedException;
import jvmmonitor.management.MemoryUsageLog;
import org.apache.log4j.Logger;

import javax.management.MalformedObjectNameException;
import java.io.IOException;
import java.util.Map;

public class Agent {

    final static Logger logger = Logger.getLogger(Agent.class);

    static String pid = "6251";

    //private static MemoryMonitor memoryMonitor = new MemoryMonitor();

    public static void main(String[] args) throws InterruptedException {


        logger.info("Currently running");
        for (VirtualMachineDescriptor vmd : VirtualMachine.list())
            logger.info(vmd.id() + "\t" + vmd.displayName());

        try {
            LogManager logManager = new LogManager(pid);
            logManager.stratMonitoring();


            while (true) {
                logManager.getMemoryUsageLog().printMemoryUsage();
                Map<String,Long> mem_usage = (Map<String,Long>)logManager.getUsageLog().get(LogManager.MEMORY_USAGE_LOG);
                logger.info(MemoryUsageLog.ALLOCATED_HEAP_MEMORY.concat(" : ").concat(String.valueOf(mem_usage.get(MemoryUsageLog.ALLOCATED_HEAP_MEMORY))));
                logger.info(MemoryUsageLog.USED_HEAP_MEMORY.concat(" : ").concat(String.valueOf(mem_usage.get(MemoryUsageLog.USED_HEAP_MEMORY))));
                logger.info(MemoryUsageLog.MAX_HEAP_MEMORY.concat(" : ").concat(String.valueOf(mem_usage.get(MemoryUsageLog.MAX_HEAP_MEMORY))));
                logger.info(MemoryUsageLog.PENDING_FINALIZATIONS.concat(" : ").concat(String.valueOf(mem_usage.get(MemoryUsageLog.PENDING_FINALIZATIONS))));

                Thread.sleep(500);

                logger.info("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AttachNotSupportedException e) {
            e.printStackTrace();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (MonitoringNotStartedException e) {
            e.printStackTrace();
        }

    }

}