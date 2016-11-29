package jvmmonitor.management;

import jvmmonitor.exceptions.UnknownMonitorTypeException;
import jvmmonitor.management.monitors.CPUUsageMonitor;
import jvmmonitor.management.monitors.GarbageCollectionMonitor;
import jvmmonitor.management.monitors.MemoryUsageMonitor;
import jvmmonitor.management.monitors.UsageMonitor;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import java.io.IOException;

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
 * Factory class to build UsageMonitors of different types
 */
public class UsageMonitorFactory {

    public static UsageMonitor getUsageMonitor(String monitorType, MBeanServerConnection connection) throws IOException,
            MalformedObjectNameException, UnknownMonitorTypeException {
        MonitorType type = MonitorType.getMonitorType(monitorType);

        UsageMonitor usageMonitor;

        switch (type) {
            case CPU_USAGE_MONITOR:
                usageMonitor = new CPUUsageMonitor(connection);
                break;
            case MEMORY_USAGE_MONITOR:
                usageMonitor = new MemoryUsageMonitor(connection);
                break;
            case GARBAGE_COLLECTION_EVENTS_MONITOR:
                usageMonitor = new GarbageCollectionMonitor(connection);
                break;
            default:
                throw new UnknownMonitorTypeException("Invalid Monitor Type : " + monitorType);
        }

        return usageMonitor;
    }
}
