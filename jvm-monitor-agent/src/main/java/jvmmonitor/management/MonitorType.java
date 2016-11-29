package jvmmonitor.management;

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
 * Usage monitor types enum
 */
public enum MonitorType {
    CPU_USAGE_MONITOR("cpu"), MEMORY_USAGE_MONITOR("memory"), GARBAGE_COLLECTION_EVENTS_MONITOR("gc");

    private String value = null;

    private MonitorType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    /**
     * get {@link MonitorType} for a given value.
     */
    public static MonitorType getMonitorType(String value) {
        for (MonitorType type : MonitorType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }

}
