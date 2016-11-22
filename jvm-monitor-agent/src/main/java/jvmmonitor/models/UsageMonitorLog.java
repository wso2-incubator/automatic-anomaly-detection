package jvmmonitor.models;

import jvmmonitor.exceptions.UnknownMonitorTypeException;
import jvmmonitor.management.models.UsageLog;

import java.util.Date;
import java.util.HashMap;

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
 * Model for all collected JVM monitoring parameters
 * <p>
 * Stores
 * MemoryUsageLog models
 * CPUUsageLog models
 * List of GarbageCollectionLog models
 * Time Stamp created at usage collection
 */
public class UsageMonitorLog {

    private HashMap<String, UsageLog> usageLogs;
    private long timeStamp;

    /**
     * Assumed that usage logs are taken when UsageMonitorLog obj created
     */
    public UsageMonitorLog() {
        this.timeStamp = new Date().getTime(); //added the time stamp at object creation
        this.usageLogs = new HashMap<>();
    }

    public void addUsageLog(String monitorType, UsageLog usageLog) {
        usageLogs.put(monitorType, usageLog);
    }

    public UsageLog getUsageLog(String monitorType) throws UnknownMonitorTypeException {
        UsageLog usageLog = usageLogs.get(monitorType);
        if (usageLog != null) {
            return usageLog;
        } else {
            throw new UnknownMonitorTypeException("No Usage Log found with monitor type : " + monitorType);
        }
    }

    public long getTimeStamp() {
        return timeStamp;
    }

}
