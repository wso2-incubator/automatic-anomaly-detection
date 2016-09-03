package jvmmonitor.management;

import com.sun.management.OperatingSystemMXBean;

import javax.management.MBeanServerConnection;
import java.io.IOException;
import java.lang.management.MemoryMXBean;
import java.util.HashMap;
import java.util.Map;

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
public class CPUUsageMonitor {

    private OperatingSystemMXBean osMXBean;

    public final static String PROCESS_CPU_LOAD = "process.load";
    public final static String SYSTEM_CPU_LOAD = "system.load";

    /**
     * Constructor
     * @param serverConnection
     */
    public CPUUsageMonitor(MBeanServerConnection serverConnection) throws IOException {
        this.osMXBean = newPlatformMXBeanProxy(serverConnection, OPERATING_SYSTEM_MXBEAN_NAME , OperatingSystemMXBean.class);
    }

    /**
     * Return CPU load precentages of the System and the process
     * @return
     */
    public Map<String , Double> getCPULoads(){

        if (osMXBean != null){
            Map<String,Double> cpuLoads = new HashMap<String, Double>();

            cpuLoads.put(PROCESS_CPU_LOAD , osMXBean.getProcessCpuLoad());
            cpuLoads.put(SYSTEM_CPU_LOAD , osMXBean.getSystemCpuLoad());

            return cpuLoads;
        }else {
            throw new NullPointerException();
        }


    }

}
