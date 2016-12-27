package jvmmonitor;

import jvmmonitor.agents.JMXUsageMonitorAgent;
import jvmmonitor.agents.SNMPUsageMonitorAgent;
import jvmmonitor.exceptions.MonitorAgentInitializationFailed;
import jvmmonitor.exceptions.UnknownMonitorAgentTypeException;
import util.JmaProperties;

/*
 *  Copyright (c) ${DATE}, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * Monitor agent factory
 */
public class UsageMonitorAgentFactory {

    /**
     * @param jmaProperties
     * @return UsageMonitorAgent
     * @throws MonitorAgentInitializationFailed
     * @throws UnknownMonitorAgentTypeException
     */
    public static UsageMonitorAgent getUsageMonitorAgent(JmaProperties jmaProperties)
            throws MonitorAgentInitializationFailed, UnknownMonitorAgentTypeException {
        String mode = jmaProperties.getMode();
        MonitorAgentType type = MonitorAgentType.getMonitorType(jmaProperties.getMode());

        // If the given type not found in MonitorAgentType throws an exception indicating that
        if (type == null) {
            throw new UnknownMonitorAgentTypeException("Invalid Monitor agent type : " + mode);
        }

        UsageMonitorAgent usageMonitorAgent;
        switch (type) {
        case JMX:
            usageMonitorAgent = new JMXUsageMonitorAgent(jmaProperties.getTargetAddress(),
                    jmaProperties.getTargetRmiServerPort(), jmaProperties.getTargetRmiRegistryPort(),
                    jmaProperties.getTargetUsername(), jmaProperties.getTargetPassword());
            break;
        case PROCESS_ID:
            usageMonitorAgent = new JMXUsageMonitorAgent(jmaProperties.getPid());
            break;
        case SNMP:
            usageMonitorAgent = new SNMPUsageMonitorAgent(jmaProperties.getSnmpAddress(), jmaProperties.getSnmpPort());
            break;
        default:
            throw new UnknownMonitorAgentTypeException("Invalid Monitor agent type : " + mode);
        }

        return usageMonitorAgent;
    }
}
