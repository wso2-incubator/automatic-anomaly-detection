package jvmmonitor;

import jvmmonitor.agents.JMXUsageMonitorAgent;
import jvmmonitor.agents.SNMPUsageMonitorAgent;
import jvmmonitor.exceptions.MonitorAgentInitializationFailed;
import jvmmonitor.exceptions.UnknownMonitorAgentTypeException;
import util.PropertyLoader;

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
public class UsageMonitorAgentFatory {

    public static UsageMonitorAgent getUsageMonitor(String monitorType) throws MonitorAgentInitializationFailed, UnknownMonitorAgentTypeException {
        MonitorAgentType type = MonitorAgentType.getMonitorType(monitorType);

        if (type == null) {
            throw new UnknownMonitorAgentTypeException("Invalid Monitor agent type : " + monitorType);
        }

        UsageMonitorAgent usageMonitorAgent;
        switch (type) {
            case JMX:
                usageMonitorAgent = new JMXUsageMonitorAgent(PropertyLoader.targetAddress,
                        PropertyLoader.targetRmiServerPort,
                        PropertyLoader.targetRmiRegistryPort,
                        PropertyLoader.targetUsername,
                        PropertyLoader.targetPassword);
                break;
            case PROCESS_ID:
                usageMonitorAgent = new JMXUsageMonitorAgent(PropertyLoader.pid);
                break;
            case SNMP:
                usageMonitorAgent = new SNMPUsageMonitorAgent(PropertyLoader.snmpAddress, PropertyLoader.snmpPort);
                break;
            default:
                throw new UnknownMonitorAgentTypeException("Invalid Monitor agent type : " + monitorType);
        }
        return usageMonitorAgent;
    }
}
