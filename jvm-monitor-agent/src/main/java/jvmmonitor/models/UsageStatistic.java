package jvmmonitor.models;

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
 * Stores all Usage Statistices in one object with the time stamp that data has been collected
 * Encapsulating all the statistics with one time stamp is the main purpose
 */
public class UsageStatistic {

    private List<CPUStatistic> cpuStatistics;
    private List<MemoryStatistic> memoryStatistics;
    private List<GarbageCollectionStatistic> garbageCollectionStatistics;
    private long timeStamp;

    public UsageStatistic(List<CPUStatistic> cpuStatistics, List<MemoryStatistic> memoryStatistics, List<GarbageCollectionStatistic> garbageCollectionStatistics) {
        this.cpuStatistics = cpuStatistics;
        this.memoryStatistics = memoryStatistics;
        this.garbageCollectionStatistics = garbageCollectionStatistics;
        this.timeStamp = new Date().getTime(); //added the time stamp at object creation

    }

    public List<CPUStatistic> getCpuStatistics() {
        return cpuStatistics;
    }

    public List<MemoryStatistic> getMemoryStatistics() {
        return memoryStatistics;
    }

    public List<GarbageCollectionStatistic> getGarbageCollectionStatistics() {
        return garbageCollectionStatistics;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
