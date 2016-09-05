package jvmmonitor.management;

import jvmmonitor.model.MemoryLog;
import org.apache.log4j.Logger;
import javax.management.MBeanServerConnection;
import java.io.IOException;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.Map;

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
 * Manage MemoryMXBeans from given JVM Connections
 */
public class MemoryUsageMonitor {

    private MemoryMXBean memoryMXBean ;

//    public final static String MAX_HEAP_MEMORY = "max_heap";
//    public final static String ALLOCATED_HEAP_MEMORY = "alloc_heap";
//    public final static String USED_HEAP_MEMORY = "used_heap";
//    public final static String MAX_NON_HEAP_MEMORY = "max_non_heap";
//    public final static String ALLOCATED_NON_HEAP_MEMORY = "alloc_non_heap";
//    public final static String USED_NON_HEAP_MEMORY = "used_non_heap";
//    public final static String PENDING_FINALIZATIONS = "pending_final";

    final static Logger logger = Logger.getLogger(MemoryUsageMonitor.class);
    /**
     * Constructor
     *
     * Create a MemoryMXBean provided the ServerConnection
     * Object created can be used to collect the Memory management data
     *
     * @param serverConnection
     * @throws InterruptedException
     * @throws IOException
     */
    public MemoryUsageMonitor(MBeanServerConnection serverConnection) throws IOException  {
        this.memoryMXBean = newPlatformMXBeanProxy(serverConnection, MEMORY_MXBEAN_NAME, MemoryMXBean.class);
    }

    /**
     * Calculate the memory management using the MemoryMXBean
     *
     * @return {Map<String, Long>} hash map with memory usages
     */
    public Map<String, Long> getMemoryUsage() {

        Map<String,Long> memUsageMap = new HashMap<String,Long>();

        MemoryUsage mu;
        MemoryLog memoryLog;

        if (memoryMXBean != null) {
            memoryLog = new MemoryLog();

            //heap memory management data
            mu = memoryMXBean.getHeapMemoryUsage();
//            memUsageMap.put(MAX_HEAP_MEMORY, mu.getMax());
//            memUsageMap.put(ALLOCATED_HEAP_MEMORY, mu.getCommitted());
//            memUsageMap.put(USED_HEAP_MEMORY, mu.getUsed());
            memoryLog.setMaxHeapMemory(mu.getMax());
            memoryLog.setAllocatedHeapMemory(mu.getCommitted());
            memoryLog.setUsedHeapMemory(mu.getUsed());


            //non heap memory management data
            mu = memoryMXBean.getNonHeapMemoryUsage();
//            memUsageMap.put(MAX_NON_HEAP_MEMORY, mu.getMax());
//            memUsageMap.put(ALLOCATED_NON_HEAP_MEMORY, mu.getCommitted());
//            memUsageMap.put(USED_NON_HEAP_MEMORY, mu.getUsed());
            memoryLog.setMaxNonHeapMemory(mu.getMax());
            memoryLog.setAllocatedNonHeapMemory(mu.getCommitted());
            memoryLog.setUsedNonHeapMemory(mu.getUsed());


//            memUsageMap.put(PENDING_FINALIZATIONS, (long) (memoryMXBean.getObjectPendingFinalizationCount()));
            memoryLog.setPendingFinalizations(memoryMXBean.getObjectPendingFinalizationCount());

            return memUsageMap;
        }else{
            throw new NullPointerException();
        }
    }

    /**
     *  @Test - Only for test purposes
     */
    public void printMemoryUsage(){

        logger.info("Heap:\t");
        MemoryUsage mu = memoryMXBean.getHeapMemoryUsage();
        logger.info("allocated " + mu.getCommitted() + ", used " + mu.getUsed() + ", max " + mu.getMax());
        logger.info("Non-Heap:\t");
        mu = memoryMXBean.getNonHeapMemoryUsage();
        logger.info("allocated " + mu.getCommitted() + ", used " + mu.getUsed() + ", max " + mu.getMax());
        logger.info("Pending Finalizations: " + memoryMXBean.getObjectPendingFinalizationCount());
    }


    //===========getters and setters====================
    public MemoryMXBean getMemoryMXBean() {
        return memoryMXBean;
    }

    public void setMemoryMXBean(MemoryMXBean memoryMXBean) {
        this.memoryMXBean = memoryMXBean;
    }
}
