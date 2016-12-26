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

package communicator;

import jvmmonitor.models.MemoryStatistic;
import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import java.util.List;

/**
 * This is send Memory statistic to DAS for every 1 second
 */
public class MemoryPublisher extends DASPublisher {

    private final static Logger logger = Logger.getLogger(MemoryPublisher.class);

    /**
     * Set default Memory usage stream
     * <p>
     * Data format must be in the following order in given types in "MemoryUsageStream":-
     * <p>
     * long    timeStamp
     * String  applicationId
     * long    maxHeapMemory
     * long    allocatedHeapMemory
     * long    usedHeapMemory
     * long    maxNonHeapMemory
     * long    allocatedNonHeapMemory
     * long    usedNonHeapMemory
     * long    pendingFinalizations
     */
    private static final String streamName = "MemoryUsageStream";
    private static final String streamVersion = "1.0.0";
    private List<MemoryStatistic> memoryStatistics;
    private long timestamp;

    /**
     * Constructor
     *
     * @param dasConfiguration
     * @throws DataEndpointAuthenticationException
     * @throws DataEndpointAgentConfigurationException
     * @throws DataEndpointException
     * @throws DataEndpointConfigurationException
     * @throws TransportException
     */
    public MemoryPublisher(DASConfiguration dasConfiguration) throws DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException, DataEndpointException, DataEndpointConfigurationException,
            TransportException {

        super(dasConfiguration);
        setDataStream(streamName, streamVersion);

        logger.info("Starting Memory Publisher; Host: " + dasConfiguration.getHost() + "\tThriftPort: "
                + dasConfiguration.getThriftPort() + "\tStreamID: " + streamName + ":" + streamVersion);

    }

    /**
     * Need to set Memory statistic data before publish data to DAS
     *
     * @param memoryStatistics
     * @param applicationId
     * @param timestamp
     */
    public void setMemoryStatistic(List<MemoryStatistic> memoryStatistics, String applicationId, long timestamp) {
        this.memoryStatistics = memoryStatistics;
        this.applicationId = applicationId;
        this.timestamp = timestamp;
    }

    /**
     * Publish Memory Usage Log data to DAS
     */
    @Override
    protected void publishEvents() {

        if (memoryStatistics != null && !memoryStatistics.isEmpty()) {
            MemoryStatistic memoryStat = memoryStatistics.get(memoryStatistics.size() - 1);

            Event event = new Event(dataStream, System.currentTimeMillis(), null, null,
                    new Object[]{timestamp,
                            applicationId,
                            memoryStat.getMaxHeapMemory(),
                            memoryStat.getAllocatedHeapMemory(),
                            memoryStat.getUsedHeapMemory(),
                            memoryStat.getMaxNonHeapMemory(),
                            memoryStat.getAllocatedNonHeapMemory(),
                            memoryStat.getUsedNonHeapMemory(),
                            memoryStat.getPendingFinalizations()
                    });

            dataPublisher.publish(event);

            //check is debug enable
            if (logger.isDebugEnabled()) {

                StringBuilder MemoryEvent = new StringBuilder();
                MemoryEvent.append("publish Memory data : ");
                MemoryEvent.append(timestamp);
                MemoryEvent.append(" , ");
                MemoryEvent.append(applicationId);
                MemoryEvent.append(" , ");
                MemoryEvent.append(memoryStat.getMaxHeapMemory());
                MemoryEvent.append(" , ");
                MemoryEvent.append(memoryStat.getAllocatedHeapMemory());
                MemoryEvent.append(" , ");
                MemoryEvent.append(memoryStat.getUsedHeapMemory());
                MemoryEvent.append(" , ");
                MemoryEvent.append(memoryStat.getMaxNonHeapMemory());
                MemoryEvent.append(" , ");
                MemoryEvent.append(memoryStat.getAllocatedNonHeapMemory());
                MemoryEvent.append(" , ");
                MemoryEvent.append(memoryStat.getUsedNonHeapMemory());
                MemoryEvent.append(" , ");
                MemoryEvent.append(memoryStat.getPendingFinalizations());

                logger.debug(MemoryEvent.toString());
            }

        }

    }


}
