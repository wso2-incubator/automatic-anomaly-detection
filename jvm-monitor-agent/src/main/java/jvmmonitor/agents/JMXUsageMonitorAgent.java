package jvmmonitor.agents;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;
import com.sun.management.OperatingSystemMXBean;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import jvmmonitor.UsageMonitorAgent;
import jvmmonitor.exceptions.AccessingUsageStatisticFailedException;
import jvmmonitor.exceptions.MonitorAgentInitializationFailed;
import jvmmonitor.models.CPUStatistic;
import jvmmonitor.models.GarbageCollectionStatistic;
import jvmmonitor.models.MemoryStatistic;
import org.apache.log4j.Logger;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static java.lang.management.ManagementFactory.MEMORY_MXBEAN_NAME;
import static java.lang.management.ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME;
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
public class JMXUsageMonitorAgent extends UsageMonitorAgent {


    private final static Logger logger = Logger.getLogger(JMXUsageMonitorAgent.class);
    private final static String CONNECTOR_ADDRESS = "com.sun.management.jmxremote.localConnectorAddress";

    private MemoryMXBean memoryMXBean;
    private OperatingSystemMXBean operatingSystemMXBean;
    private List<GarbageCollectorMXBean> garbageCollectorMXBeans;

    private final LinkedList<GarbageCollectionStatistic> garbageCollectionStatistics;
    private long jvmStartTime;
    private final String targetedApplicationId;


    public JMXUsageMonitorAgent(String pid) throws MonitorAgentInitializationFailed {
        try {
            //attach VM using the pid given
            VirtualMachine vm = VirtualMachine.attach(pid);

            //getting the connector address to the local JVM
            String connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
            if (connectorAddress == null) {
                Properties props = vm.getSystemProperties();
                String home = props.getProperty("java.home");
                String agent = home + File.separator + "lib" + File.separator + "management-agent.jar";
                vm.loadAgent(agent);

                connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
                while (connectorAddress == null) {
                    try {
                        Thread.sleep(1000);
                        connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
                    } catch (InterruptedException e) {
                        logger.error("Retrieving connector address from the Connected VM(PID : " + pid + ") failed. Trying to reconnect.", e);
                    }
                }
            }
            vm.detach();

            logger.info("JMX Address for given PID :" + pid + " is :" + connectorAddress);
            MBeanServerConnection connection = JMXConnectorFactory.connect(new JMXServiceURL(connectorAddress)).getMBeanServerConnection();

            garbageCollectionStatistics = new LinkedList<>();
            targetedApplicationId = getAppNameFromPID(pid);
            getMXBeans(connection);

        } catch (AttachNotSupportedException | IOException | AgentLoadException | AgentInitializationException | MalformedObjectNameException e) {
            throw new MonitorAgentInitializationFailed(e.getMessage(), e);
        }


    }

    public JMXUsageMonitorAgent(String hostname, String rmiServerPort, String rmiRegistryPort) throws MonitorAgentInitializationFailed {
        String jmxURL = createJMXURL(hostname, rmiServerPort, rmiRegistryPort);
        logger.info("Trying to connect : " + jmxURL);
        try {
            MBeanServerConnection connection = JMXConnectorFactory.connect(new JMXServiceURL(jmxURL)).getMBeanServerConnection();

            garbageCollectionStatistics = new LinkedList<>();
            targetedApplicationId = getAppNameFromPID(hostname + ":" + rmiServerPort);
            getMXBeans(connection); //get mxBeans using the server connection
        } catch (IOException | MalformedObjectNameException e) {
            throw new MonitorAgentInitializationFailed(e.getMessage(), e);
        }


    }

    public JMXUsageMonitorAgent(String hostname, String rmiServerPort, String rmiRegistryPort, String username, String password) throws MonitorAgentInitializationFailed {
        String jmxURL = createJMXURL(hostname, rmiServerPort, rmiRegistryPort);
        logger.info("Trying to connect : " + jmxURL);
        try {
            Map<String, String[]> credential = new HashMap<String, String[]>();
            credential.put(JMXConnector.CREDENTIALS, new String[]{username, password});
            MBeanServerConnection connection = JMXConnectorFactory.connect(new JMXServiceURL(jmxURL), credential).getMBeanServerConnection();

            garbageCollectionStatistics = new LinkedList<>();
            targetedApplicationId = getAppNameFromPID(hostname + ":" + rmiServerPort);
            getMXBeans(connection); //get mxBeans using the server connection
        } catch (IOException | MalformedObjectNameException e) {
            throw new MonitorAgentInitializationFailed(e.getMessage(), e);
        }
    }

    @Override
    public List<CPUStatistic> getCPUStatistics() throws AccessingUsageStatisticFailedException {

        if (operatingSystemMXBean != null) {
            ArrayList<CPUStatistic> cpuStatistics = new ArrayList<>(1);

            CPUStatistic cpuStatistic = new CPUStatistic();
            cpuStatistic.setProcessCPULoad(operatingSystemMXBean.getProcessCpuLoad());
            cpuStatistic.setSystemCPULoad(operatingSystemMXBean.getSystemCpuLoad());
            cpuStatistics.add(cpuStatistic);

            return cpuStatistics;
        } else {
            throw new AccessingUsageStatisticFailedException("Can't access CPU statistics : Operating System MXBean is null");
        }
    }

    @Override
    public List<MemoryStatistic> getMemoryStatistics() throws AccessingUsageStatisticFailedException {

        if (memoryMXBean != null) {
            ArrayList<MemoryStatistic> memoryStatistics = new ArrayList<>(1);

            MemoryUsage mu;
            MemoryStatistic memoryStatistic;

            memoryStatistic = new MemoryStatistic();

            //heap memory management data
            mu = memoryMXBean.getHeapMemoryUsage();
            memoryStatistic.setMaxHeapMemory(mu.getMax());
            memoryStatistic.setAllocatedHeapMemory(mu.getCommitted());
            memoryStatistic.setUsedHeapMemory(mu.getUsed());

            //non heap memory management data
            mu = memoryMXBean.getNonHeapMemoryUsage();
            memoryStatistic.setMaxNonHeapMemory(mu.getMax());
            memoryStatistic.setAllocatedNonHeapMemory(mu.getCommitted());
            memoryStatistic.setUsedNonHeapMemory(mu.getUsed());

            memoryStatistic.setPendingFinalizations(memoryMXBean.getObjectPendingFinalizationCount());

            memoryStatistics.add(memoryStatistic);
            return memoryStatistics;
        } else {
            throw new AccessingUsageStatisticFailedException("Can't access Memory statistics : Memory MXBean is null");
        }
    }

    @Override
    public synchronized List<GarbageCollectionStatistic> getGarbageCollectionStatistics() throws AccessingUsageStatisticFailedException {
        if (garbageCollectionStatistics.size() > 0) {
            ArrayList<GarbageCollectionStatistic> gcStatistics = new ArrayList<>(garbageCollectionStatistics.size());
            while (garbageCollectionStatistics.size() > 0) {
                gcStatistics.add(garbageCollectionStatistics.poll());
            }

            return gcStatistics;
        }
        return null;
    }

    @Override
    public String getTargetedApplicationId() {
        return targetedApplicationId;
    }


    private String createJMXURL(String hostname, String rmiServerPort, String rmiRegistryPort) {
        if (hostname != null) {
            String jmxURL, RMI_Server_Address, RMI_Registry_Address;

            //create JMX URL
            RMI_Server_Address = hostname.concat(":").concat(rmiServerPort);
            RMI_Registry_Address = hostname.concat(":").concat(rmiRegistryPort);

            jmxURL = "service:agents:rmi://"
                    .concat(RMI_Server_Address)
                    .concat("/jndi/rmi://")
                    .concat(RMI_Registry_Address)
                    .concat("/jmxrmi");

            return jmxURL;
        }
        return null;
    }

    private void getMXBeans(MBeanServerConnection connection) throws IOException, MalformedObjectNameException {

        operatingSystemMXBean = newPlatformMXBeanProxy(connection, OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
        memoryMXBean = newPlatformMXBeanProxy(connection, MEMORY_MXBEAN_NAME, MemoryMXBean.class);

        //retrieving gcBeans
        Set<ObjectName> gcNames = connection.queryNames(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=*"), null);
        garbageCollectorMXBeans = new ArrayList<>(gcNames.size());

        for (ObjectName gcName : gcNames) {
            GarbageCollectorMXBean garbageCollectorMXBean = ManagementFactory.newPlatformMXBeanProxy(connection, gcName.toString(), GarbageCollectorMXBean.class);
            garbageCollectorMXBeans.add(garbageCollectorMXBean);

            NotificationEmitter emitter = (NotificationEmitter) garbageCollectorMXBean;
            NotificationListener listener = new GCNotificationListener();
            emitter.addNotificationListener(listener, null, null);

        }

        this.jvmStartTime = ManagementFactory.newPlatformMXBeanProxy(connection, ManagementFactory.RUNTIME_MXBEAN_NAME, RuntimeMXBean.class).getStartTime();
        logger.info("Start time jvm " + jvmStartTime);
    }


    /**
     * Get Display name of given PID
     *
     * @param pid - Process Id of targeted application
     * @return
     */
    private String getAppNameFromPID(String pid) {

        String appName = null;
        for (VirtualMachineDescriptor vmd : VirtualMachine.list()) {
            if (pid != null && pid.equals(vmd.id())) {
                appName = vmd.displayName();
                logger.info("AppName found. PID: " + vmd.id() + "\tName: " + vmd.displayName());
                break;
            }
        }
        appName = appName + "(" + pid + ")";
        return appName;
    }

    /**
     * Implements a notification listener to listen notifications happens after Garbage collection
     */
    private class GCNotificationListener implements NotificationListener {

        private final static String EDEN_SPACE = "PS Eden Space";
        private final static String SURVIVOR_SPACE = "PS Survivor Space";
        private final static String OLD_GENERATION_SPACE = "PS Old Gen";

        /**
         * implement the notifier callback handler
         * when gc event happens this method will be executed
         * collect gc log data and add it to gc log queue
         * trigger the GarbageCollectionListeners
         *
         * @param notification
         * @param handback
         */
        public void handleNotification(Notification notification, Object handback) {

            //only handle GARBAGE_COLLECTION_NOTIFICATION notifications here
            if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {

                //get the information associated with this notification
                GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());

                //get all the info
                String gctype = info.getGcAction();

                if ("end of minor GC".equals(gctype)) {
                    gctype = "minor";
                } else if ("end of major GC".equals(gctype)) {
                    gctype = "major";
                }

                GarbageCollectionStatistic gclog = new GarbageCollectionStatistic();
                GcInfo gcInfo;
                Map<String, MemoryUsage> memoryUsageMap;
                MemoryUsage memoryUsage;

                gcInfo = info.getGcInfo();

                logger.info("GC collected >> GC type :".concat(gctype).concat(" GC Start Time :")
                        .concat(String.valueOf(gcInfo.getStartTime())));

                //=========memory Usage After GC==============================
                memoryUsageMap = gcInfo.getMemoryUsageAfterGc();

                //eden space memory management
                memoryUsage = memoryUsageMap.get(EDEN_SPACE);
                gclog.setEdenCommittedMemoryAfterGC(memoryUsage.getCommitted());
                gclog.setEdenMaxMemoryAfterGC(memoryUsage.getMax());
                gclog.setEdenUsedMemoryAfterGC(memoryUsage.getUsed());

                //survivor space memory management
                memoryUsage = memoryUsageMap.get(SURVIVOR_SPACE);
                gclog.setSurvivorCommittedMemoryAfterGC(memoryUsage.getCommitted());
                gclog.setSurvivorMaxMemoryAfterGC(memoryUsage.getMax());
                gclog.setSurvivorUsedMemoryAfterGC(memoryUsage.getUsed());

                //old gen space memory management
                memoryUsage = memoryUsageMap.get(OLD_GENERATION_SPACE);
                gclog.setOldGenCommittedMemoryAfterGC(memoryUsage.getCommitted());
                gclog.setOldGenMaxMemoryAfterGC(memoryUsage.getMax());
                gclog.setOldGenUsedMemoryAfterGC(memoryUsage.getUsed());

                //===========memory management before GC============================
                memoryUsageMap = gcInfo.getMemoryUsageBeforeGc();

                //eden space memory management
                memoryUsage = memoryUsageMap.get(EDEN_SPACE);
                gclog.setEdenCommittedMemoryBeforeGC(memoryUsage.getCommitted());
                gclog.setEdenMaxMemoryBeforeGC(memoryUsage.getMax());
                gclog.setEdenUsedMemoryBeforeGC(memoryUsage.getUsed());

                //survivor space memory management
                memoryUsage = memoryUsageMap.get(SURVIVOR_SPACE);
                gclog.setSurvivorCommittedMemoryBeforeGC(memoryUsage.getCommitted());
                gclog.setSurvivorMaxMemoryBeforeGC(memoryUsage.getMax());
                gclog.setSurvivorUsedMemoryBeforeGC(memoryUsage.getUsed());

                //old gen space memory management
                memoryUsage = memoryUsageMap.get(OLD_GENERATION_SPACE);
                gclog.setOldGenCommittedMemoryBeforeGC(memoryUsage.getCommitted());
                gclog.setOldGenMaxMemoryBeforeGC(memoryUsage.getMax());
                gclog.setOldGenUsedMemoryBeforeGC(memoryUsage.getUsed());

                //=====================general info===========================
                gclog.setDuration(gcInfo.getDuration());
                gclog.setGcCause(info.getGcCause());
                gclog.setStartTime(gcInfo.getEndTime() + jvmStartTime);
                gclog.setGcType(gctype);
                //============================================================

                synchronized (garbageCollectionStatistics) {
                    garbageCollectionStatistics.add(gclog);
                }
            }
        }
    }
}
