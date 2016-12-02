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
import org.wso2.carbon.databridge.commons.utils.DataBridgeCommonsUtils;

import java.util.List;

/**
 * This is send Memory statistic to DAS for every 1 second
 */
public class MemoryPublisher extends DASPublisher implements Runnable {

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
     * @param dasConfigurations
     * @throws DataEndpointAuthenticationException
     * @throws DataEndpointAgentConfigurationException
     * @throws DataEndpointException
     * @throws DataEndpointConfigurationException
     * @throws TransportException
     */
    public MemoryPublisher(DASConfigurations dasConfigurations) throws DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException, DataEndpointException, DataEndpointConfigurationException,
            TransportException {

        super(dasConfigurations);
        setDataStream(streamName, streamVersion);
        logger.info("Starting DAS Memory Publisher");
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

            logger.info("publish Memory data : " + timestamp + " , " + applicationId + " , "
                    + memoryStat.getMaxHeapMemory() + " , " + memoryStat.getAllocatedHeapMemory()
                    + " , " + memoryStat.getUsedHeapMemory() + " , " + memoryStat.getMaxNonHeapMemory()
                    + " , " + memoryStat.getAllocatedNonHeapMemory() + " , " + memoryStat.getUsedNonHeapMemory()
                    + " , " + memoryStat.getPendingFinalizations());
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
