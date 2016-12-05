package jvmmonitor;

import jvmmonitor.exceptions.AccessingUsageStatisticFailedException;
import jvmmonitor.models.CPUStatistic;
import jvmmonitor.models.GarbageCollectionStatistic;
import jvmmonitor.models.MemoryStatistic;

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
 * Abstract Usage monitor agent which should be implemented in order to define new monitor agents
 */
public abstract class UsageMonitorAgent {

    /**
     * Returns a list of CPU usage statistics {@link CPUStatistic}
     *
     * @return
     * @throws AccessingUsageStatisticFailedException
     */
    public abstract List<CPUStatistic> getCPUStatistics() throws AccessingUsageStatisticFailedException;

    /**
     * Returns a list of Memory usage statistics {@link MemoryStatistic}
     *
     * @return
     * @throws AccessingUsageStatisticFailedException
     */
    public abstract List<MemoryStatistic> getMemoryStatistics() throws AccessingUsageStatisticFailedException;

    /**
     * Returns a list of Garbage Collection statistics {@link GarbageCollectionStatistic}
     *
     * @return
     * @throws AccessingUsageStatisticFailedException
     */
    public abstract List<GarbageCollectionStatistic> getGarbageCollectionStatistics()
            throws AccessingUsageStatisticFailedException;

    /**
     * Returns identifications to identify the monitored applications
     *
     * @return
     */
    public abstract String getTargetedApplicationId();


}
