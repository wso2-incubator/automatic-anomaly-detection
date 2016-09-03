package jvmmonitor;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import jvmmonitor.exceptions.MonitoringNotStartedException;
import jvmmonitor.management.GarbageCollectionMonitor;
import jvmmonitor.management.MemoryUsageLog;
import jvmmonitor.server.Connection;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
 * Monitor the JVM usage metrics using the Usage Log classes
 */
public class LogManager {

    private Connection connection;
    private GarbageCollectionMonitor garbageCollectionLog;
    private MemoryUsageLog memoryUsageLog;

    public final static String MEMORY_USAGE_LOG = "mem usage";
    public final static String GARBAGE_COLLECTION_LOG = "gc_usage";
    /**
     * Constructor
     * @param pid
     * @throws IOException
     * @throws AttachNotSupportedException
     */
    public LogManager(String pid) throws IOException, AttachNotSupportedException {
        this.connection = Connection.getConnection(pid);
    }

    /**
     * Start monitoring usage metrics of JVM
     *
     * @return
     * @throws MalformedObjectNameException
     * @throws InterruptedException
     */
    public boolean stratMonitoring() throws MalformedObjectNameException, InterruptedException {

        MBeanServerConnection serverConnection;
        try {

            serverConnection = this.connection.getServerConnection();
            if (serverConnection != null) {
                this.garbageCollectionLog = new GarbageCollectionMonitor(serverConnection);
                this.memoryUsageLog = new MemoryUsageLog(serverConnection);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AgentLoadException e) {
            e.printStackTrace();
        } catch (AgentInitializationException e) {
            e.printStackTrace();
        }

        System.out.println("Currently running");
        for (VirtualMachineDescriptor vmd : VirtualMachine.list())
            System.out.println(vmd.id() + "\t" + vmd.displayName());

        return false;
    }

    /**
     * get usage data of JVMd
     * @return
     * @throws MonitoringNotStartedException
     */
    public Map<String, Object> getUsageLog() throws MonitoringNotStartedException {

        if (memoryUsageLog != null && garbageCollectionLog != null){
            Map<String , Object> usages = new HashMap<String, Object>();

            usages.put(MEMORY_USAGE_LOG , memoryUsageLog.getMemoryUsage());
            usages.put(GARBAGE_COLLECTION_LOG , garbageCollectionLog.popGCUsages());

            return usages;
        }
        else{
            throw new MonitoringNotStartedException();
        }
    }

    public MemoryUsageLog getMemoryUsageLog() {
        return memoryUsageLog;
    }
}
