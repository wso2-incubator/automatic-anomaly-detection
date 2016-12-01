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

import jvmmonitor.management.models.GarbageCollectionLog;
import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import org.wso2.carbon.databridge.commons.utils.DataBridgeCommonsUtils;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;

/**
 *
 */
public class GCPublisher extends DASPublisher {

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

    /**
     * Constructor
     *
     * @param defaultThriftPort
     * @param username
     * @param password
     * @throws SocketException
     * @throws UnknownHostException
     * @throws DataEndpointAuthenticationException
     * @throws DataEndpointAgentConfigurationException
     * @throws TransportException
     * @throws DataEndpointException
     * @throws DataEndpointConfigurationException
     */
    public GCPublisher(String hostname, int defaultThriftPort, int securePort, String username, String password) throws
            SocketException,
            UnknownHostException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointException,
            DataEndpointConfigurationException {

        super(hostname, defaultThriftPort, securePort, username, password);
        setDataStream(streamName, streamVersion);
        logger.info("Starting DAS GC Publisher");

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
    void publishLogEvents(DataPublisher dataPublisher, String streamId, String appID, GarbageCollectionLog gcLog) throws DataEndpointException,
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

        logger.info("publish GC data : " + gcLog.getStartTime() + " , " + appID + " , " + gcLog.getGcType() + " , " + gcLog.getGcCause() + " , " +
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
     * Send data to EventPublisher
     *
     * @param garbageCollectionLog
     * @throws DataEndpointAuthenticationException
     * @throws DataEndpointAgentConfigurationException
     * @throws DataEndpointException
     * @throws DataEndpointConfigurationException
     * @throws TransportException
     */
    public void publishGCData(LinkedList<GarbageCollectionLog> garbageCollectionLog) throws DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            DataEndpointException,
            DataEndpointConfigurationException,
            TransportException {

        while (!garbageCollectionLog.isEmpty()) {
            publishLogEvents(dataPublisher, dataStream, appID, garbageCollectionLog.poll());
        }

    }

    @Override
    protected void setDataStream(String streamName, String streamVersion) {
        dataStream = DataBridgeCommonsUtils.generateStreamId(streamName, streamVersion);
    }
}
