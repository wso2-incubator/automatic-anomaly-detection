package jvmmonitor.management;

import jvmmonitor.model.MemoryUsageLog;
import org.apache.log4j.Logger;

import javax.management.MBeanServerConnection;
import java.io.IOException;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import static java.lang.management.ManagementFactory.MEMORY_MXBEAN_NAME;
import static java.lang.management.ManagementFactory.newPlatformMXBeanProxy;

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



/**
 * Collect MemoryMXBeans from given JVM Connections
 */
public class MemoryUsageMonitor {

    private MemoryMXBean memoryMXBean ;

    final static Logger logger = Logger.getLogger(MemoryUsageMonitor.class);

    /**
     * Constructor
     *
     * Create a MemoryMXBean provided the ServerConnection
     * Object created can be used to collect the Memory management data
     *
     * @param serverConnection
     * @throws IOException
     */
    public MemoryUsageMonitor(MBeanServerConnection serverConnection) throws IOException  {
        this.memoryMXBean = newPlatformMXBeanProxy(serverConnection, MEMORY_MXBEAN_NAME, MemoryMXBean.class);
    }


    /**
     * Collect the memory usages using the MemoryMXBean
     * @return {MemoryUsageLog} Memory usage log model with memory usages
     */
    public MemoryUsageLog getMemoryUsage() {

        MemoryUsage mu;
        MemoryUsageLog memoryUsageLog;

        if (memoryMXBean != null) {
            memoryUsageLog = new MemoryUsageLog();

            //heap memory management data
            mu = memoryMXBean.getHeapMemoryUsage();

            memoryUsageLog.setMaxHeapMemory(mu.getMax());
            memoryUsageLog.setAllocatedHeapMemory(mu.getCommitted());
            memoryUsageLog.setUsedHeapMemory(mu.getUsed());


            //non heap memory management data
            mu = memoryMXBean.getNonHeapMemoryUsage();

            memoryUsageLog.setMaxNonHeapMemory(mu.getMax());
            memoryUsageLog.setAllocatedNonHeapMemory(mu.getCommitted());
            memoryUsageLog.setUsedNonHeapMemory(mu.getUsed());

            memoryUsageLog.setPendingFinalizations(memoryMXBean.getObjectPendingFinalizationCount());

            return memoryUsageLog;

        }else{
            throw new NullPointerException();
        }
    }


    //===========getters and setters====================
    public MemoryMXBean getMemoryMXBean() {
        return memoryMXBean;
    }

    public void setMemoryMXBean(MemoryMXBean memoryMXBean) {
        this.memoryMXBean = memoryMXBean;
    }
}
