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

    private final static Logger logger = Logger.getLogger(UsageMonitor.class);

    private Connection connection;
    private GarbageCollectionMonitor garbageCollectionMonitor;
    private MemoryUsageMonitor memoryUsageMonitor;
    private CPUUsageMonitor cpuUsageMonitor;


    /**
     * Constructor
     */
    public UsageMonitor() {
        this.connection = Connection.getConnection();
    }

    /**
     *
     * Start monitoring usage metrics of JVM
     * <p>
     * Create a Local MBean Server Connection to the targeted JVM
     * Get the required MXBeans from targeted JVM
     *
     * @param pid - process id of targeted JVM
     * @return - boolean to express if the startMonitoring was successful
     * @throws MalformedObjectNameException
     * @throws InterruptedException
     * @throws AgentInitializationException
     * @throws AgentLoadException
     * @throws AttachNotSupportedException
     * @throws IOException
     */
    public boolean stratMonitoring(String pid) throws MalformedObjectNameException,
            InterruptedException,
            AgentInitializationException,
            AgentLoadException,
            AttachNotSupportedException,
            IOException {

        MBeanServerConnection serverConnection = connection.getLocalMBeanServerConnection(pid);
        return getMXBeans(serverConnection);
    }

    /**
     *
     * Start monitoring usage metrics of JVM
     * <p>
     * Create a Remote MBean Server Connection to the targeted JVM
     * Get the required MXBeans from targeted JVM
     *
     * @param hostname - hostname(ip) of targeted JVM
     * @param RMI_Server_Port -Java RMI connection server port
     * @param RMI_Registry_Port -Java RMI connection registry port
     * @param username - access role of the JMX service for authorization
     * @param password - password of the access role for authentication
     * @return - boolean to express if the startMonitoring was successful
     * @throws MalformedObjectNameException
     * @throws InterruptedException
     * @throws AgentInitializationException
     * @throws AgentLoadException
     * @throws AttachNotSupportedException
     * @throws IOException
     */
    public boolean stratMonitoring(String hostname,String RMI_Server_Port, String RMI_Registry_Port, String username, String password) throws MalformedObjectNameException,
            InterruptedException,
            AgentInitializationException,
            AgentLoadException,
            AttachNotSupportedException,
            IOException {

        MBeanServerConnection serverConnection = connection.getRemoteMBeanServerConnection(hostname,RMI_Server_Port,RMI_Registry_Port,username,password);
        return getMXBeans(serverConnection);
    }

    /**
     * Create and assign Monitor objs with MXBeans to monitor the targeted JVM
     * @param serverConnection - connection to get MXBeans from the targeted machine
     * @return - return true if method executed successfully
     * @throws MalformedObjectNameException
     */
    private boolean getMXBeans(MBeanServerConnection serverConnection) throws MalformedObjectNameException {
        try {

            if (serverConnection != null) {
                this.garbageCollectionMonitor = new GarbageCollectionMonitor(serverConnection);
                this.memoryUsageMonitor = new MemoryUsageMonitor(serverConnection);
                this.cpuUsageMonitor = new CPUUsageMonitor(serverConnection);
                return true;
            }
        } catch (IOException e) {
            logger.error("Failed to get MXBeans using the MBeanServerConnection");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * get usage data of JVMd
     *
     * @return - return usage data bundle at the requested time
     * @throws MonitoringNotStartedException
     */
    public UsageMonitorLog getUsageLog() throws MonitoringNotStartedException {

        //if all the monitoring metrics are available, return them using UsageMonitorLog model
        if (memoryUsageMonitor != null && garbageCollectionMonitor != null && cpuUsageMonitor != null) {
            UsageMonitorLog usageMonitorLog = new UsageMonitorLog(memoryUsageMonitor.getMemoryUsage(), garbageCollectionMonitor.getGCUsages(), cpuUsageMonitor.getCPULoads());
            return usageMonitorLog;
        } else {
            throw new MonitoringNotStartedException();
        }

    }

    /**
     * This method can be used to register GarbageCollectionListener implementations
     * The listeners will be notified by calling their processGCLogs method
     *
     * @param listener - listener obj which can listen to garbage collection log events( must implement {@link GarbageCollectionListener} )
     * @throws MonitoringNotStartedException
     */
    public void registerGCNotifications(GarbageCollectionListener listener) throws MonitoringNotStartedException {

        if (garbageCollectionMonitor != null) {
            this.garbageCollectionMonitor.registerListener(listener);
        } else {
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


    // ===================Getters=======================
    public GarbageCollectionMonitor getGarbageCollectionMonitor() {
        return garbageCollectionMonitor;
    }

    public MemoryUsageMonitor getMemoryUsageMonitor() {
        return memoryUsageMonitor;
    }

    public CPUUsageMonitor getCpuUsageMonitor() {
        return cpuUsageMonitor;
    }
}
