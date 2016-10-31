package jvmmonitor.management;

import com.sun.management.OperatingSystemMXBean;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import jvmmonitor.server.Connection;

import java.io.IOException;
import java.lang.management.ManagementFactory;

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
public class CPUUsageLog {


    static String pid = "7565";
    public static void main(String[] args) throws InterruptedException, AgentLoadException, IOException, AgentInitializationException, AttachNotSupportedException {

        System.out.println("Currently running");
        for (VirtualMachineDescriptor vmd : VirtualMachine.list())
            System.out.println(vmd.id() + "\t" + vmd.displayName());


        Connection connection = Connection.getConnection();

        OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        bean = newPlatformMXBeanProxy(connection.getLocalMBeanServerConnection(pid), OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
        while (true) {
            System.out.println( "Process CPU load : " +bean.getProcessCpuLoad()*100 + "%");
            System.out.println( "System CPU load : " + bean.getSystemCpuLoad()*100 + "%");
            System.out.println( "Process CPU Time: " + bean.getProcessCpuTime());

            System.out.println( "System Load Average: " + bean.getSystemLoadAverage());

            Thread.sleep(1000);
        }

    }
}
