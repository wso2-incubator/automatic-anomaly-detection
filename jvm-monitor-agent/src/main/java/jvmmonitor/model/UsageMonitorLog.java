package jvmmonitor.model;

import java.util.Date;
import java.util.List;

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
 *
 * Stores
 *  MemoryUsageLog model
 *  CPUUsageLog model
 *  List of GarbageCollectionLog model
 *  Time Stamp created at usage collection
 */
public class UsageMonitorLog {

    private MemoryUsageLog memoryUsageLog;
    private List<GarbageCollectionLog> garbageCollectionLog;
    private CPULoadLog cpuLoadLog;
    private long timeStamp;


    /**
     * Constructor
     *
     * Forced to add usage models at object creation time
     *
     * @param memoryLog
     * @param garbageCollectionLog
     * @param cpuLoadLog
     */
    public UsageMonitorLog(MemoryUsageLog memoryLog, List<GarbageCollectionLog> garbageCollectionLog, CPULoadLog cpuLoadLog) {
        this.memoryUsageLog = memoryLog;
        this.garbageCollectionLog = garbageCollectionLog;
        this.cpuLoadLog = cpuLoadLog;
        this.timeStamp = new Date().getTime(); //added the time stamp at object creation
    }


    public MemoryUsageLog getMemoryUsageLog() {
        return memoryUsageLog;
    }

    public void setMemoryUsageLog(MemoryUsageLog memoryUsageLog) {
        this.memoryUsageLog = memoryUsageLog;
    }

    public List<GarbageCollectionLog> getGarbageCollectionLog() {
        return garbageCollectionLog;
    }

    public void setGarbageCollectionLog(List<GarbageCollectionLog> garbageCollectionLog) {
        this.garbageCollectionLog = garbageCollectionLog;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public CPULoadLog getCpuLoadLog() {
        return cpuLoadLog;
    }

    public void setCpuLoadLog(CPULoadLog cpuLoadLog) {
        this.cpuLoadLog = cpuLoadLog;
    }
}
