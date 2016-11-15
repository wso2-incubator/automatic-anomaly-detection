/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package communicator;

import jvmmonitor.model.CPULoadLog;
import jvmmonitor.model.GarbageCollectionLog;
import jvmmonitor.model.MemoryUsageLog;
import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.TransportException;

/**
 * This class creates Event according to Usage data and send to DataPublisher
 */
public class EventPublisher {

    private final static Logger LOGGER = Logger.getLogger(EventPublisher.class);

    /**
     * This method publish CPU Load Log data to DAS
     *
     * @param dataPublisher
     * @param streamId
     * @param timestamp
     * @param appID
     * @param cpuLog
     * @throws DataEndpointException
     * @throws DataEndpointAuthenticationException
     * @throws DataEndpointAgentConfigurationException
     * @throws TransportException
     * @throws DataEndpointConfigurationException
     */
    public void publishLogEvents(DataPublisher dataPublisher, String streamId, long timestamp, String appID, CPULoadLog cpuLog) throws DataEndpointException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointConfigurationException {

        Event event = new Event(streamId, System.currentTimeMillis(), null, null,
                new Object[]{timestamp,
                        appID,
                        cpuLog.getProcessCPULoad(),
                        cpuLog.getSystemCPULoad()
                });

        dataPublisher.publish(event);

        LOGGER.info("publish CPU data : " + timestamp + " , " + appID + " , " + cpuLog.getProcessCPULoad() + " , " + cpuLog.getSystemCPULoad());

    }

    /**
     * This method publish Garbage Collection Log data to DAS
     *
     * @param dataPublisher
     * @param streamId
     * @param appID
     * @param gcLog
     * @throws DataEndpointException
     * @throws DataEndpointAuthenticationException
     * @throws DataEndpointAgentConfigurationException
     * @throws TransportException
     * @throws DataEndpointConfigurationException
     */
    public void publishLogEvents(DataPublisher dataPublisher, String streamId, String appID, GarbageCollectionLog gcLog) throws DataEndpointException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointConfigurationException {

        Event event = new Event(streamId, System.currentTimeMillis(), null, null,
                new Object[]{gcLog.getStartTime(),
                        appID,
                        gcLog.getGcType(),
                        gcLog.getGcCause(),
                        gcLog.getDuration(),
                        gcLog.getEdenUsedMemoryAfterGC(),
                        gcLog.getEdenUsedMemoryBeforeGC(),
                        gcLog.getSurvivorUsedMemoryAfterGC(),
                        gcLog.getSurvivorUsedMemoryBeforeGC(),
                        gcLog.getOldGenUsedMemoryAfterGC(),
                        gcLog.getOldGenUsedMemoryBeforeGC(),
                        gcLog.getEdenCommittedMemoryAfterGC(),
                        gcLog.getEdenCommittedMemoryBeforeGC(),
                        gcLog.getSurvivorCommittedMemoryAfterGC(),
                        gcLog.getSurvivorCommittedMemoryBeforeGC(),
                        gcLog.getOldGenCommittedMemoryAfterGC(),
                        gcLog.getOldGenCommittedMemoryBeforeGC(),
                        gcLog.getEdenMaxMemoryAfterGC(),
                        gcLog.getEdenMaxMemoryBeforeGC(),
                        gcLog.getSurvivorMaxMemoryAfterGC(),
                        gcLog.getSurvivorMaxMemoryBeforeGC(),
                        gcLog.getOldGenMaxMemoryAfterGC(),
                        gcLog.getOldGenMaxMemoryBeforeGC()});

        dataPublisher.publish(event);

        LOGGER.info("publish GC data : " + gcLog.getStartTime() + " , " + appID + " , " + gcLog.getGcType() + " , " + gcLog.getGcCause() + " , " +
                gcLog.getDuration() + " , " + gcLog.getEdenUsedMemoryAfterGC() + " , " + gcLog.getEdenUsedMemoryBeforeGC()
                + " , " + gcLog.getSurvivorUsedMemoryAfterGC() + " , " + gcLog.getSurvivorUsedMemoryBeforeGC()
                + " , " + gcLog.getOldGenUsedMemoryAfterGC() + " , " + gcLog.getOldGenUsedMemoryBeforeGC()
                + " , " + gcLog.getEdenCommittedMemoryAfterGC() + " , " + gcLog.getEdenCommittedMemoryBeforeGC()
                + " , " + gcLog.getSurvivorCommittedMemoryAfterGC() + " , " + gcLog.getSurvivorCommittedMemoryBeforeGC()
                + " , " + gcLog.getOldGenCommittedMemoryAfterGC() + " , " + gcLog.getOldGenCommittedMemoryBeforeGC()
                + " , " + gcLog.getEdenMaxMemoryAfterGC() + " , " + gcLog.getEdenMaxMemoryBeforeGC()
                + " , " + gcLog.getSurvivorMaxMemoryAfterGC() + " , " + gcLog.getSurvivorMaxMemoryBeforeGC()
                + " , " + gcLog.getOldGenMaxMemoryAfterGC() + " , " + gcLog.getOldGenMaxMemoryBeforeGC());

    }

    /**
     * This method publish Memory Usage Log data to DAS
     *
     * @param dataPublisher
     * @param streamId
     * @param timestamp
     * @param appID
     * @param memoryLog
     * @throws DataEndpointException
     * @throws DataEndpointAuthenticationException
     * @throws DataEndpointAgentConfigurationException
     * @throws TransportException
     * @throws DataEndpointConfigurationException
     */
    public void publishLogEvents(DataPublisher dataPublisher, String streamId, long timestamp, String appID, MemoryUsageLog memoryLog) throws DataEndpointException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointConfigurationException {

        Event event = new Event(streamId, System.currentTimeMillis(), null, null,
                new Object[]{timestamp,
                        appID,
                        memoryLog.getMaxHeapMemory(),
                        memoryLog.getAllocatedHeapMemory(),
                        memoryLog.getUsedHeapMemory(),
                        memoryLog.getMaxNonHeapMemory(),
                        memoryLog.getAllocatedNonHeapMemory(),
                        memoryLog.getUsedNonHeapMemory(),
                        memoryLog.getPendingFinalizations()
                });

        dataPublisher.publish(event);

        LOGGER.info("publish Memory data : " + timestamp + " , " + appID + " , " + memoryLog.getMaxHeapMemory() + " , " + memoryLog.getAllocatedHeapMemory()
                + " , " + memoryLog.getUsedHeapMemory() + " , " + memoryLog.getMaxNonHeapMemory() + " , " + memoryLog.getAllocatedNonHeapMemory()
                + " , " + memoryLog.getUsedNonHeapMemory() + " , " + memoryLog.getPendingFinalizations());

    }


}
