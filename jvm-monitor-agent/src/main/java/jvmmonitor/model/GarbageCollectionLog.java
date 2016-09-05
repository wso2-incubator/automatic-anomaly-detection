package jvmmonitor.model;

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
public class GarbageCollectionLog {

    private long EdenUsedMemoryAfterGC;
    private long EdenUsedMemoryBeforeGC;
    private long EdenCommittedMemoryAfterGC;
    private long EdenCommittedMemoryBeforeGC;
    private long EdenMaxMemoryAfterGC;
    private long EdenMaxMemoryBeforeGC;

    private long SurvivorUsedMemoryAfterGC;
    private long SurvivorUsedMemoryBeforeGC;
    private long SurvivorCommittedMemoryAfterGC;
    private long SurvivorCommittedMemoryBeforeGC;
    private long SurvivorMaxMemoryAfterGC;
    private long SurvivorMaxMemoryBeforeGC;

    private long OldGenUsedMemoryAfterGC;
    private long OldGenUsedMemoryBeforeGC;
    private long OldGenCommittedMemoryAfterGC;
    private long OldGenCommittedMemoryBeforeGC;
    private long OldGenMaxMemoryAfterGC;
    private long OldGenMaxMemoryBeforeGC;

    private String gcType;
    private long startTime;
    private double duration;
    private String gcCause;

    public long getEdenUsedMemoryAfterGC() {
        return EdenUsedMemoryAfterGC;
    }

    public void setEdenUsedMemoryAfterGC(long edenUsedMemoryAfterGC) {
        EdenUsedMemoryAfterGC = edenUsedMemoryAfterGC;
    }

    public long getEdenUsedMemoryBeforeGC() {
        return EdenUsedMemoryBeforeGC;
    }

    public void setEdenUsedMemoryBeforeGC(long edenUsedMemoryBeforeGC) {
        EdenUsedMemoryBeforeGC = edenUsedMemoryBeforeGC;
    }

    public long getEdenCommittedMemoryAfterGC() {
        return EdenCommittedMemoryAfterGC;
    }

    public void setEdenCommittedMemoryAfterGC(long edenCommittedMemoryAfterGC) {
        EdenCommittedMemoryAfterGC = edenCommittedMemoryAfterGC;
    }

    public long getEdenCommittedMemoryBeforeGC() {
        return EdenCommittedMemoryBeforeGC;
    }

    public void setEdenCommittedMemoryBeforeGC(long edenCommittedMemoryBeforeGC) {
        EdenCommittedMemoryBeforeGC = edenCommittedMemoryBeforeGC;
    }

    public long getEdenMaxMemoryAfterGC() {
        return EdenMaxMemoryAfterGC;
    }

    public void setEdenMaxMemoryAfterGC(long edenMaxMemoryAfterGC) {
        EdenMaxMemoryAfterGC = edenMaxMemoryAfterGC;
    }

    public long getEdenMaxMemoryBeforeGC() {
        return EdenMaxMemoryBeforeGC;
    }

    public void setEdenMaxMemoryBeforeGC(long edenMaxMemoryBeforeGC) {
        EdenMaxMemoryBeforeGC = edenMaxMemoryBeforeGC;
    }

    public long getSurvivorUsedMemoryAfterGC() {
        return SurvivorUsedMemoryAfterGC;
    }

    public void setSurvivorUsedMemoryAfterGC(long survivorUsedMemoryAfterGC) {
        SurvivorUsedMemoryAfterGC = survivorUsedMemoryAfterGC;
    }

    public long getSurvivorUsedMemoryBeforeGC() {
        return SurvivorUsedMemoryBeforeGC;
    }

    public void setSurvivorUsedMemoryBeforeGC(long survivorUsedMemoryBeforeGC) {
        SurvivorUsedMemoryBeforeGC = survivorUsedMemoryBeforeGC;
    }

    public long getSurvivorCommittedMemoryAfterGC() {
        return SurvivorCommittedMemoryAfterGC;
    }

    public void setSurvivorCommittedMemoryAfterGC(long survivorCommittedMemoryAfterGC) {
        SurvivorCommittedMemoryAfterGC = survivorCommittedMemoryAfterGC;
    }

    public long getSurvivorCommittedMemoryBeforeGC() {
        return SurvivorCommittedMemoryBeforeGC;
    }

    public void setSurvivorCommittedMemoryBeforeGC(long survivorCommittedMemoryBeforeGC) {
        SurvivorCommittedMemoryBeforeGC = survivorCommittedMemoryBeforeGC;
    }

    public long getSurvivorMaxMemoryAfterGC() {
        return SurvivorMaxMemoryAfterGC;
    }

    public void setSurvivorMaxMemoryAfterGC(long survivorMaxMemoryAfterGC) {
        SurvivorMaxMemoryAfterGC = survivorMaxMemoryAfterGC;
    }

    public long getSurvivorMaxMemoryBeforeGC() {
        return SurvivorMaxMemoryBeforeGC;
    }

    public void setSurvivorMaxMemoryBeforeGC(long survivorMaxMemoryBeforeGC) {
        SurvivorMaxMemoryBeforeGC = survivorMaxMemoryBeforeGC;
    }

    public long getOldGenUsedMemoryAfterGC() {
        return OldGenUsedMemoryAfterGC;
    }

    public void setOldGenUsedMemoryAfterGC(long oldGenUsedMemoryAfterGC) {
        OldGenUsedMemoryAfterGC = oldGenUsedMemoryAfterGC;
    }

    public long getOldGenUsedMemoryBeforeGC() {
        return OldGenUsedMemoryBeforeGC;
    }

    public void setOldGenUsedMemoryBeforeGC(long oldGenUsedMemoryBeforeGC) {
        OldGenUsedMemoryBeforeGC = oldGenUsedMemoryBeforeGC;
    }

    public long getOldGenCommittedMemoryAfterGC() {
        return OldGenCommittedMemoryAfterGC;
    }

    public void setOldGenCommittedMemoryAfterGC(long oldGenCommittedMemoryAfterGC) {
        OldGenCommittedMemoryAfterGC = oldGenCommittedMemoryAfterGC;
    }

    public long getOldGenCommittedMemoryBeforeGC() {
        return OldGenCommittedMemoryBeforeGC;
    }

    public void setOldGenCommittedMemoryBeforeGC(long oldGenCommittedMemoryBeforeGC) {
        OldGenCommittedMemoryBeforeGC = oldGenCommittedMemoryBeforeGC;
    }

    public long getOldGenMaxMemoryAfterGC() {
        return OldGenMaxMemoryAfterGC;
    }

    public void setOldGenMaxMemoryAfterGC(long oldGenMaxMemoryAfterGC) {
        OldGenMaxMemoryAfterGC = oldGenMaxMemoryAfterGC;
    }

    public long getOldGenMaxMemoryBeforeGC() {
        return OldGenMaxMemoryBeforeGC;
    }

    public void setOldGenMaxMemoryBeforeGC(long oldGenMaxMemoryBeforeGC) {
        OldGenMaxMemoryBeforeGC = oldGenMaxMemoryBeforeGC;
    }

    public String getGcType() {
        return gcType;
    }

    public void setGcType(String gcType) {
        this.gcType = gcType;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getGcCause() {
        return gcCause;
    }

    public void setGcCause(String gcCause) {
        this.gcCause = gcCause;
    }
}
