package jvmmonitor;

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
 * Monitor agent types
 */
public enum MonitorAgentType {
    JMX("jmx"), PROCESS_ID("pid"), SNMP("snmp");

    private String value = null;

    MonitorAgentType(String value) {
        this.value = value;
    }

    /**
     * get {@link MonitorAgentType} for a given value.
     */
    public static MonitorAgentType getMonitorType(String value) {
        for (MonitorAgentType type : MonitorAgentType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }

    public String getValue() {
        return this.value;
    }

}
