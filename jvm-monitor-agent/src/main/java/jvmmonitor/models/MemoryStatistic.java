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
package jvmmonitor.models;

/**
 * Memory usage models class
 * <p>
 * Store
 * Heap memory parameters
 * Non-Heap memory parameters
 */
public class MemoryStatistic {

    private long maxHeapMemory;
    private long allocatedHeapMemory;
    private long usedHeapMemory;
    private long maxNonHeapMemory;
    private long allocatedNonHeapMemory;
    private long usedNonHeapMemory;
    private long pendingFinalizations;

    public MemoryStatistic() {
        this.maxHeapMemory = 0;
        this.allocatedHeapMemory = 0;
        this.usedHeapMemory = 0;
        this.allocatedNonHeapMemory = 0;
        this.maxNonHeapMemory = 0;
        this.usedNonHeapMemory = 0;
        this.pendingFinalizations = 0;
    }

    public long getMaxHeapMemory() {
        return maxHeapMemory;
    }

    public void setMaxHeapMemory(long maxHeapMemory) {
        this.maxHeapMemory = maxHeapMemory;
    }

    public long getAllocatedHeapMemory() {
        return allocatedHeapMemory;
    }

    public void setAllocatedHeapMemory(long allocatedHeapMemory) {
        this.allocatedHeapMemory = allocatedHeapMemory;
    }

    public long getUsedHeapMemory() {
        return usedHeapMemory;
    }

    public void setUsedHeapMemory(long usedHeapMemory) {
        this.usedHeapMemory = usedHeapMemory;
    }

    public long getMaxNonHeapMemory() {
        return maxNonHeapMemory;
    }

    public void setMaxNonHeapMemory(long maxNonHeapMemory) {
        this.maxNonHeapMemory = maxNonHeapMemory;
    }

    public long getAllocatedNonHeapMemory() {
        return allocatedNonHeapMemory;
    }

    public void setAllocatedNonHeapMemory(long allocatedNonHeapMemory) {
        this.allocatedNonHeapMemory = allocatedNonHeapMemory;
    }

    public long getUsedNonHeapMemory() {
        return usedNonHeapMemory;
    }

    public void setUsedNonHeapMemory(long usedNonHeapMemory) {
        this.usedNonHeapMemory = usedNonHeapMemory;
    }

    public long getPendingFinalizations() {
        return pendingFinalizations;
    }

    public void setPendingFinalizations(long pendingFinalizations) {
        this.pendingFinalizations = pendingFinalizations;
    }
}
