package communicator;

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

import jvmmonitor.io.ExtractGCData;
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class creates Event according to Usage data and send to DataPublisher
 */
public class EventPublisher {

    final static Logger logger = Logger.getLogger(EventPublisher.class);

    /**
     * This method publish CPU Load Log data to DAS
     *
     * @param dataPublisher
     * @param streamId
     * @param date
     * @param cpuLog
     * @throws DataEndpointException
     * @throws DataEndpointAuthenticationException
     * @throws DataEndpointAgentConfigurationException
     * @throws TransportException
     * @throws DataEndpointConfigurationException
     */
    public void publishLogEvents(DataPublisher dataPublisher, String streamId, long date, CPULoadLog cpuLog) throws DataEndpointException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointConfigurationException {

        Event event = new Event(streamId, System.currentTimeMillis(), null, null,
                new Object[]{cpuLog.getProcessCPULoad(), cpuLog.getSystemCPULoad(), date});

        dataPublisher.publish(event);

        logger.info("publish CPU data : " + cpuLog.getProcessCPULoad() + " , " + cpuLog.getSystemCPULoad() + " , " + date);

    }

    /**
     * This method publish Garbage Collection Log data to DAS
     *
     * @param dataPublisher
     * @param streamId
     * @param gcLog
     * @throws DataEndpointException
     * @throws DataEndpointAuthenticationException
     * @throws DataEndpointAgentConfigurationException
     * @throws TransportException
     * @throws DataEndpointConfigurationException
     */
    public void publishLogEvents(DataPublisher dataPublisher, String streamId, GarbageCollectionLog gcLog) throws DataEndpointException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointConfigurationException {

        Event event = new Event(streamId, System.currentTimeMillis(), null, null,
                new Object[]{gcLog.getGcType(),
                        gcLog.getDuration(),
                        gcLog.getStartTime(),
                        gcLog.getGcCause(),
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

        logger.info("publish GC data : " + gcLog.getGcType() + " , " + gcLog.getDuration() + " , " + gcLog.getStartTime()
                + " , " + gcLog.getGcCause() + " , " + gcLog.getEdenUsedMemoryAfterGC() + " , " + gcLog.getEdenUsedMemoryBeforeGC()
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
     * @param date
     * @param memoryLog
     * @throws DataEndpointException
     * @throws DataEndpointAuthenticationException
     * @throws DataEndpointAgentConfigurationException
     * @throws TransportException
     * @throws DataEndpointConfigurationException
     */
    public void publishLogEvents(DataPublisher dataPublisher, String streamId, long date, MemoryUsageLog memoryLog) throws DataEndpointException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointConfigurationException {

        Event event = new Event(streamId, System.currentTimeMillis(), null, null,
                new Object[]{memoryLog.getMaxHeapMemory(),
                        memoryLog.getAllocatedHeapMemory(),
                        memoryLog.getUsedHeapMemory(),
                        memoryLog.getMaxNonHeapMemory(),
                        memoryLog.getAllocatedNonHeapMemory(),
                        memoryLog.getUsedNonHeapMemory(),
                        memoryLog.getPendingFinalizations(),
                        date});

        dataPublisher.publish(event);

        logger.info("publish Memory data : " + memoryLog.getMaxHeapMemory() + " , " + memoryLog.getAllocatedHeapMemory()
                + " , " + memoryLog.getUsedHeapMemory() + " , " + memoryLog.getMaxNonHeapMemory() + " , " + memoryLog.getAllocatedNonHeapMemory()
                + " , " + memoryLog.getUsedNonHeapMemory() + " , " + memoryLog.getPendingFinalizations() + " , " + date);

    }

    /**
     * This method publish Garbage Collection Log data from file to DAS
     *
     * @param dataPublisher
     * @param streamId
     * @param fileName
     * @throws DataEndpointException
     * @throws DataEndpointAuthenticationException
     * @throws DataEndpointAgentConfigurationException
     * @throws TransportException
     * @throws DataEndpointConfigurationException
     * @throws FileNotFoundException
     */
    public void publishLogEvents(DataPublisher dataPublisher, String streamId, String fileName) throws DataEndpointException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointConfigurationException,
            FileNotFoundException {

        Scanner scanner = new Scanner(new FileInputStream(fileName));
        while (scanner.hasNextLine()) {
            String stringLog = scanner.nextLine();

            ExtractGCData eObj = new ExtractGCData();
            ArrayList gcData = eObj.getGCData(stringLog);

            if (gcData == null) {
                continue;
            }

            Event event = new Event(streamId, System.currentTimeMillis(), null, null,
                    new Object[]{gcData.get(0), gcData.get(1), gcData.get(2), gcData.get(3), gcData.get(4),
                            gcData.get(5), gcData.get(6), gcData.get(7), gcData.get(8), gcData.get(9), gcData.get(10),
                            gcData.get(11), gcData.get(12), gcData.get(13), gcData.get(14), gcData.get(15), gcData.get(16),
                            gcData.get(17), gcData.get(18), gcData.get(19), gcData.get(20), gcData.get(21)});

            logger.info("publish GC log data from file : " + gcData.get(0) + " , " + gcData.get(1) + " , " + gcData.get(2) + " , " + gcData.get(3)
                    + " , " + gcData.get(4) + " , " + gcData.get(5) + " , " + gcData.get(6) + " , " + gcData.get(7) + " , " + gcData.get(8)
                    + " , " + gcData.get(9) + " , " + gcData.get(10) + " , " + gcData.get(11) + " , " + gcData.get(12) + " , " + gcData.get(13)
                    + " , " + gcData.get(14) + " , " + gcData.get(15) + " , " + gcData.get(16) + " , " + gcData.get(17) + " , " + gcData.get(18)
                    + " , " + gcData.get(19) + " , " + gcData.get(20) + " , " + gcData.get(21));

            dataPublisher.publish(event);

        }
        scanner.close();

    }


}
