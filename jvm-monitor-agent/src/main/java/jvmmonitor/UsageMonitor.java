package jvmmonitor;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import jvmmonitor.exceptions.MonitoringNotStartedException;
import jvmmonitor.management.CPUUsageMonitor;
import jvmmonitor.management.GarbageCollectionMonitor;
import jvmmonitor.management.MemoryUsageMonitor;
import jvmmonitor.model.UsageMonitorLog;
import jvmmonitor.server.Connection;
import jvmmonitor.util.GarbageCollectionListener;
import org.apache.log4j.Logger;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import java.io.IOException;
import java.util.ArrayList;

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
 * Monitor the JVM usage metrics using the Usage monitoring classes
 * Return an Usage Log object
 */
public class UsageMonitor {

    private Connection connection;
    private GarbageCollectionMonitor garbageCollectionMonitor;
    private MemoryUsageMonitor memoryUsageMonitor;
    private CPUUsageMonitor cpuUsageMonitor;

    final static Logger logger = Logger.getLogger(UsageMonitor.class);

    /**
     * Constructor
     *
     * At creation the PID of the monitoring JVM should be provided
     * The JVM will be searched using the PID
     *
     * @param pid
     * @throws IOException
     * @throws AttachNotSupportedException
     */
    public UsageMonitor(String pid) throws IOException,
            AttachNotSupportedException {

        this.connection = Connection.getConnection(pid);
    }

    /**
     * Start monitoring usage metrics of JVM
     *
     * Create a MBean Server Connection to the targeted JVM
     * Get the required MXBeans from targeted JVM
     *
     * @return
     * @throws MalformedObjectNameException
     * @throws InterruptedException
     */
    public boolean stratMonitoring() throws MalformedObjectNameException,
            InterruptedException {

        MBeanServerConnection serverConnection;
        try {

            serverConnection = this.connection.getServerConnection();
            if (serverConnection != null) {
                this.garbageCollectionMonitor = new GarbageCollectionMonitor(serverConnection);
                this.memoryUsageMonitor = new MemoryUsageMonitor(serverConnection);
                this.cpuUsageMonitor = new CPUUsageMonitor(serverConnection);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AgentLoadException e) {
            e.printStackTrace();
        } catch (AgentInitializationException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * get usage data of JVMd
     * @return
     * @throws MonitoringNotStartedException
     */
    public UsageMonitorLog getUsageLog() throws MonitoringNotStartedException {

        //if all the monitoring metrics are available, return them using UsageMonitorLog model
        if (memoryUsageMonitor != null && garbageCollectionMonitor != null && cpuUsageMonitor != null){
            UsageMonitorLog usageMonitorLog = new UsageMonitorLog(memoryUsageMonitor.getMemoryUsage(),garbageCollectionMonitor.getGCUsages(), cpuUsageMonitor.getCPULoads());
            return usageMonitorLog;
        }
        else{
            throw new MonitoringNotStartedException();
        }

    }

    /**
     * This method can be used to register GarbageCollectionListener implementations
     * The listeners will be notified by calling their processGCLogs method
     * @param listener
     * @throws MonitoringNotStartedException
     */
    public void registerGCNotifications(GarbageCollectionListener listener) throws MonitoringNotStartedException {

        if (garbageCollectionMonitor != null){
            this.garbageCollectionMonitor.registerListener(listener);
        }else{
            throw new MonitoringNotStartedException();
        }

    }


    /**
     * Display currently running JVMs in the same machine
     */
    public void displayCurrentlyRunningJVMs() {
        logger.info("Currently running");
        for (VirtualMachineDescriptor vmd : VirtualMachine.list())
            logger.info(vmd.id() + "\t" + vmd.displayName());

    }

        // ====================================Getters=======================
    public GarbageCollectionMonitor getGarbageCollectionMonitor() {
        return garbageCollectionMonitor;
    }

    public void setCredential(String[] credential){
        connection.setCredential(credential);
    }
    public MemoryUsageMonitor getMemoryUsageMonitor() {
        return memoryUsageMonitor;
    }

    public CPUUsageMonitor getCpuUsageMonitor() {
        return cpuUsageMonitor;
    }
}
