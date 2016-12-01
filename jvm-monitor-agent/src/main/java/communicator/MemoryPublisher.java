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

import jvmmonitor.exceptions.UnknownMonitorTypeException;
import jvmmonitor.management.MonitorType;
import jvmmonitor.management.models.MemoryUsageLog;
import jvmmonitor.models.UsageMonitorLog;
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


public class MemoryPublisher extends DASPublisher implements Runnable {

    private final static Logger logger = Logger.getLogger(MemoryPublisher.class);
    private UsageMonitorLog usageLogObj;

    /**
     * Set default Memory usage stream
     * <p>
     * Data format must be in the following order in given types in "MemoryUsageStream":-
     * <p>
     * long    timeStamp
     * String  appID
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
    public MemoryPublisher(String hostname, int defaultThriftPort, int securePort, String username, String password) throws
            SocketException,
            UnknownHostException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointException,
            DataEndpointConfigurationException {

        super(hostname, defaultThriftPort, securePort, username, password);
        setDataStream(streamName, streamVersion);
        logger.info("Starting DAS Memory Publisher");
    }

    /**
     * Need to set UsageMonitorLog before publish data to DAS
     *
     * @param usageLogObj
     */
    public void setUsageLogObj(UsageMonitorLog usageLogObj) {
        this.usageLogObj = usageLogObj;
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
    void publishLogEvents(DataPublisher dataPublisher, String streamId, long timestamp, String appID, MemoryUsageLog memoryLog) throws DataEndpointException,
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

        logger.info("publish Memory data : " + timestamp + " , " + appID + " , " + memoryLog.getMaxHeapMemory() + " , " + memoryLog.getAllocatedHeapMemory()
                + " , " + memoryLog.getUsedHeapMemory() + " , " + memoryLog.getMaxNonHeapMemory() + " , " + memoryLog.getAllocatedNonHeapMemory()
                + " , " + memoryLog.getUsedNonHeapMemory() + " , " + memoryLog.getPendingFinalizations());

    }

    @Override
    public void run() {

        try {
            //Send data to EventPublisher
            publishLogEvents(dataPublisher, dataStream, usageLogObj.getTimeStamp(), appID, (MemoryUsageLog) usageLogObj.getUsageLog(MonitorType.MEMORY_USAGE_MONITOR.getValue()));

        } catch (DataEndpointConfigurationException e) {
            logger.error(e.getMessage(), e);
        } catch (DataEndpointAgentConfigurationException e) {
            logger.error(e.getMessage(), e);
        } catch (DataEndpointException e) {
            logger.error(e.getMessage(), e);
        } catch (TransportException e) {
            logger.error(e.getMessage(), e);
        } catch (DataEndpointAuthenticationException e) {
            logger.error(e.getMessage(), e);
        } catch (UnknownMonitorTypeException e) {
            logger.error(e.getMessage(), e);
        }

    }

    @Override
    protected void setDataStream(String streamName, String streamVersion) {
        dataStream = DataBridgeCommonsUtils.generateStreamId(streamName, streamVersion);
    }
}
