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

import jvmmonitor.models.GarbageCollectionStatistic;
import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import org.wso2.carbon.databridge.commons.utils.DataBridgeCommonsUtils;

import java.util.List;

/**
 * This is send Garbage collection statistic to DAS for every 100 millisecond
 */
public class GCPublisher extends DASPublisher implements Runnable {

    private final static Logger logger = Logger.getLogger(GCPublisher.class);

    /**
     * Set default Garbage collection log Stream
     * <p>
     * Data format must be in the following order in given types in "GarbageCollectionStream":-
     * <p>
     * String  gcType
     * long    duration
     * long    startTime
     * String  gcCause
     * <p>
     * long    EdenUsedMemoryAfterGC
     * long    EdenUsedMemoryBeforeGC
     * long    EdenCommittedMemoryAfterGC
     * long    EdenCommittedMemoryBeforeGC
     * long    EdenMaxMemoryAfterGC
     * long    EdenMaxMemoryBeforeGC
     * <p>
     * long    SurvivorUsedMemoryAfterGC
     * long    SurvivorUsedMemoryBeforeGC
     * long    SurvivorCommittedMemoryAfterGC
     * long    SurvivorCommittedMemoryBeforeGC
     * long    SurvivorMaxMemoryAfterGC
     * long    SurvivorMaxMemoryBeforeGC
     * <p>
     * long    OldGenUsedMemoryAfterGC
     * long    OldGenUsedMemoryBeforeGC
     * long    OldGenCommittedMemoryAfterGC
     * long    OldGenCommittedMemoryBeforeGC
     * long    OldGenMaxMemoryAfterGC
     * long    OldGenMaxMemoryBeforeGC
     */
    private static final String streamName = "GarbageCollectionStream";
    private static final String streamVersion = "1.0.0";
    private List<GarbageCollectionStatistic> garbageCollectionStatistics;
    private long timestamp;

    /**
     * Constructor
     *
     * @param dasConfigurations
     * @throws DataEndpointAuthenticationException
     * @throws DataEndpointAgentConfigurationException
     * @throws DataEndpointException
     * @throws DataEndpointConfigurationException
     * @throws TransportException
     */
    public GCPublisher(DASConfigurations dasConfigurations) throws DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException, DataEndpointException, DataEndpointConfigurationException,
            TransportException {

        super(dasConfigurations);
        setDataStream(streamName, streamVersion);

        logger.info("Starting GC Publisher; Host: " + dasConfigurations.getHost() + "\tThriftPort: "
                + dasConfigurations.getThriftPort() + "\tStreamName: " + streamName + ":" + streamVersion);

    }

    /**
     * Need to set GC statistic data before publish data to DAS
     *
     * @param garbageCollectionStatistics
     * @param applicationId
     * @param timestamp
     */
    public void setGarbageCollectionStatistic(List<GarbageCollectionStatistic> garbageCollectionStatistics
            , String applicationId, long timestamp) {

        this.garbageCollectionStatistics = garbageCollectionStatistics;
        this.applicationId = applicationId;
        this.timestamp = timestamp;

    }

    /**
     * Publish Garbage Collection Log data to DAS
     */
    @Override
    protected void publishEvents() {

        if (garbageCollectionStatistics != null) {
            for (GarbageCollectionStatistic gcStat : garbageCollectionStatistics) {

                Event event = new Event(dataStream, System.currentTimeMillis(), null, null,
                        new Object[]{timestamp,
                                applicationId,
                                gcStat.getGcType(),
                                gcStat.getGcCause(),
                                gcStat.getDuration(),
                                gcStat.getEdenUsedMemoryAfterGC(),
                                gcStat.getEdenUsedMemoryBeforeGC(),
                                gcStat.getSurvivorUsedMemoryAfterGC(),
                                gcStat.getSurvivorUsedMemoryBeforeGC(),
                                gcStat.getOldGenUsedMemoryAfterGC(),
                                gcStat.getOldGenUsedMemoryBeforeGC(),
                                gcStat.getEdenCommittedMemoryAfterGC(),
                                gcStat.getEdenCommittedMemoryBeforeGC(),
                                gcStat.getSurvivorCommittedMemoryAfterGC(),
                                gcStat.getSurvivorCommittedMemoryBeforeGC(),
                                gcStat.getOldGenCommittedMemoryAfterGC(),
                                gcStat.getOldGenCommittedMemoryBeforeGC(),
                                gcStat.getEdenMaxMemoryAfterGC(),
                                gcStat.getEdenMaxMemoryBeforeGC(),
                                gcStat.getSurvivorMaxMemoryAfterGC(),
                                gcStat.getSurvivorMaxMemoryBeforeGC(),
                                gcStat.getOldGenMaxMemoryAfterGC(),
                                gcStat.getOldGenMaxMemoryBeforeGC()});

                dataPublisher.publish(event);

                logger.info("publish GC data : " + gcStat.getStartTime() + " , " + applicationId
                        + " , " + gcStat.getGcType()
                        + " , " + gcStat.getGcCause()
                        + " , " + gcStat.getDuration()
                        + " , " + gcStat.getEdenUsedMemoryAfterGC()
                        + " , " + gcStat.getEdenUsedMemoryBeforeGC()
                        + " , " + gcStat.getSurvivorUsedMemoryAfterGC()
                        + " , " + gcStat.getSurvivorUsedMemoryBeforeGC()
                        + " , " + gcStat.getOldGenUsedMemoryAfterGC()
                        + " , " + gcStat.getOldGenUsedMemoryBeforeGC()
                        + " , " + gcStat.getEdenCommittedMemoryAfterGC()
                        + " , " + gcStat.getEdenCommittedMemoryBeforeGC()
                        + " , " + gcStat.getSurvivorCommittedMemoryAfterGC()
                        + " , " + gcStat.getSurvivorCommittedMemoryBeforeGC()
                        + " , " + gcStat.getOldGenCommittedMemoryAfterGC()
                        + " , " + gcStat.getOldGenCommittedMemoryBeforeGC()
                        + " , " + gcStat.getEdenMaxMemoryAfterGC()
                        + " , " + gcStat.getEdenMaxMemoryBeforeGC()
                        + " , " + gcStat.getSurvivorMaxMemoryAfterGC()
                        + " , " + gcStat.getSurvivorMaxMemoryBeforeGC()
                        + " , " + gcStat.getOldGenMaxMemoryAfterGC()
                        + " , " + gcStat.getOldGenMaxMemoryBeforeGC());
            }
        }

    }

    @Override
    protected void setDataStream(String streamName, String streamVersion) {
        dataStream = DataBridgeCommonsUtils.generateStreamId(streamName, streamVersion);
    }

    @Override
    public void run() {
        publishEvents();
    }

}
