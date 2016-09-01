package monitor.beans;

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
import org.omg.CORBA.PUBLIC_MEMBER;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.IOException;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.lang.management.ManagementFactory.MEMORY_MXBEAN_NAME;
import static java.lang.management.ManagementFactory.newPlatformMXBeanProxy;

/**
 * Manage MemoryMXBeans from given JVM Connections
 */
public class MemoryBean {

    private MemoryMXBean memoryMXBean ;

    public final static String MAX_HEAP_MEMORY = "max_heap";
    public final static String ALLOCATED_HEAP_MEMORY = "alloc_heap";
    public final static String USED_HEAP_MEMORY = "used_heap";
    public final static String MAX_NON_HEAP_MEMORY = "max_non_heap";
    public final static String ALLOCATED_NON_HEAP_MEMORY = "alloc_non_heap";
    public final static String USED_NON_HEAP_MEMORY = "used_non_heap";
    public final static String PENDING_FINALIZATIONS = "pending_final";


    /**
     * Constructor
     *
     * Create a MemoryMXBean provided the ServerConnection
     * Object created can be used to collect the Memory usage data
     *
     * @param serverConnection
     * @throws InterruptedException
     * @throws IOException
     */
    public MemoryBean(MBeanServerConnection serverConnection) throws InterruptedException, IOException  {
        this.memoryMXBean = newPlatformMXBeanProxy(serverConnection, MEMORY_MXBEAN_NAME, MemoryMXBean.class);
    }

    /**
     * Calculate the memory usage using the MemoryMXBean
     *
     * @return {Map<String, Long>} hash map with memory usages
     */
    public Map<String, Long> getMemoryUsage(){

        Map<String,Long> memUsageMap = new HashMap<>();

        MemoryUsage mu;

        //heap memory usage data
        mu = memoryMXBean.getHeapMemoryUsage();
        memUsageMap.put(MAX_HEAP_MEMORY,mu.getMax());
        memUsageMap.put(ALLOCATED_HEAP_MEMORY,mu.getCommitted());
        memUsageMap.put(USED_HEAP_MEMORY,mu.getUsed() );

        //non heap memory usage data
        mu = memoryMXBean.getNonHeapMemoryUsage();
        memUsageMap.put(MAX_NON_HEAP_MEMORY,mu.getMax());
        memUsageMap.put(ALLOCATED_NON_HEAP_MEMORY,mu.getCommitted());
        memUsageMap.put(USED_NON_HEAP_MEMORY,mu.getUsed());

        memUsageMap.put(PENDING_FINALIZATIONS,(long)(memoryMXBean.getObjectPendingFinalizationCount()));

        return memUsageMap;
    }


    /**
     *  @Test - Only for test purposes
     */
    public void printMemoryUsage(){

        System.out.print("Heap:\t");
        MemoryUsage mu = memoryMXBean.getHeapMemoryUsage();
        System.out.println("allocated " + mu.getCommitted() + ", used " + mu.getUsed() + ", max " + mu.getMax());
        System.out.print("Non-Heap:\t");
        mu = memoryMXBean.getNonHeapMemoryUsage();
        System.out.println("allocated " + mu.getCommitted() + ", used " + mu.getUsed() + ", max " + mu.getMax());
        System.out.println("Pending Finalizations: " + memoryMXBean.getObjectPendingFinalizationCount());
    }

}
