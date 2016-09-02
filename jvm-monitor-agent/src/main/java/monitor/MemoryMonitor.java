package monitor;

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

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.IOException;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import static java.lang.management.ManagementFactory.MEMORY_MXBEAN_NAME;
import static java.lang.management.ManagementFactory.newPlatformMXBeanProxy;

public class MemoryMonitor {


    static final String CONNECTOR_ADDRESS =
            "com.sun.management.jmxremote.localConnectorAddress";


    public boolean printStats(String id) throws InterruptedException {
        try {

            VirtualMachine vm = VirtualMachine.attach(id);
            System.out.println("Connected to " + vm.id());
            System.out.println("System Properties:");
            for (Map.Entry<?, ?> en : vm.getSystemProperties().entrySet())
                System.out.println("\t" + en.getKey() + " = " + en.getValue());
            System.out.println();
            try {

                MBeanServerConnection sc = connect(vm);
                MemoryMXBean memoryMXBean = newPlatformMXBeanProxy(sc, MEMORY_MXBEAN_NAME, MemoryMXBean.class);
                System.out.println();

                while (true) {
                    getRamInfoHtml(memoryMXBean);
                    Thread.sleep(1000);
                }

            } catch (AgentLoadException ex) {
                System.out.println("JMX: " + ex);
            } catch (AgentInitializationException ex) {
                System.out.println("JMX: " + ex);
            }
            vm.detach();
            return true;
        } catch (AttachNotSupportedException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }


    static MBeanServerConnection connect(VirtualMachine vm)
            throws AgentLoadException, AgentInitializationException, IOException {
        String connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
        if (connectorAddress == null) {
            System.out.println("loading agent");
            Properties props = vm.getSystemProperties();
            String home = props.getProperty("java.home");
            String agent = home + File.separator + "lib" + File.separator + "management-agent.jar";
            vm.loadAgent(agent);
            connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
            while (connectorAddress == null) try {
                Thread.sleep(1000);
                connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
            } catch (InterruptedException ex) {
            }
        }
        JMXConnector c = JMXConnectorFactory.connect(new JMXServiceURL(connectorAddress));
        return c.getMBeanServerConnection();
    }


    private void printRamInfoHtml(MemoryMXBean memoryMXBean) {
        System.out.print("Heap:\t");
        MemoryUsage mu = memoryMXBean.getHeapMemoryUsage();
        System.out.println("allocated " + mu.getCommitted() + ", used " + mu.getUsed() + ", max " + mu.getMax());
        System.out.print("Non-Heap:\t");
        mu = memoryMXBean.getNonHeapMemoryUsage();
        System.out.println("allocated " + mu.getCommitted() + ", used " + mu.getUsed() + ", max " + mu.getMax());
        System.out.println("Pending Finalizations: " + memoryMXBean.getObjectPendingFinalizationCount());
    }


    public MemoryMXBean getMemoryBean(String id) {
        try {

            VirtualMachine vm = VirtualMachine.attach(id);
            System.out.println("Connected to " + vm.id());
            System.out.println("System Properties:");
            for (Map.Entry<?, ?> en : vm.getSystemProperties().entrySet())
                System.out.println("\t" + en.getKey() + " = " + en.getValue());
            System.out.println();
            try {
                MBeanServerConnection sc = connect(vm);
                MemoryMXBean memoryMXBean = newPlatformMXBeanProxy(sc, MEMORY_MXBEAN_NAME, MemoryMXBean.class);
                return memoryMXBean;

            } catch (AgentLoadException ex) {
                System.out.println("JMX: " + ex);
            } catch (AgentInitializationException ex) {
                System.out.println("JMX: " + ex);
            }
            vm.detach();

        } catch (AttachNotSupportedException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public ArrayList getRamInfoHtml(MemoryMXBean memoryMXBean) {
        ArrayList gcData = new ArrayList(7);

        System.out.print("Heap:\t");
        MemoryUsage mu = memoryMXBean.getHeapMemoryUsage();
        System.out.println("allocated " + mu.getCommitted() + ", used " + mu.getUsed() + ", max " + mu.getMax());
        gcData.add(mu.getCommitted());
        gcData.add(mu.getUsed());
        gcData.add(mu.getMax());

        System.out.print("Non-Heap:\t");
        mu = memoryMXBean.getNonHeapMemoryUsage();
        System.out.println("allocated " + mu.getCommitted() + ", used " + mu.getUsed() + ", max " + mu.getMax());
        gcData.add(mu.getCommitted());
        gcData.add(mu.getUsed());
        gcData.add(mu.getMax());
        System.out.println("Pending Finalizations: " + memoryMXBean.getObjectPendingFinalizationCount());
        gcData.add(memoryMXBean.getObjectPendingFinalizationCount());

        return gcData;
    }
}
