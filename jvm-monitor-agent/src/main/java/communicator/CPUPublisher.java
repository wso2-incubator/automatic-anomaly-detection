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

import jvmmonitor.models.CPUStatistic;
import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.TransportException;

import java.util.List;

/**
 * This is send CPU statistic to DAS for every 1 second
 */
public class CPUPublisher extends DASPublisher {

    private final static Logger logger = Logger.getLogger(CPUPublisher.class);

    /**
     * Set default CPU usage stream
     * <p>
     * Data format must be in the following order in given types in "CPUUsageStream":-
     * <p>
     * long      timeStamp
     * String    applicationId
     * double    processCPULoad
     * double    systemCPULoad
     */
    private static final String streamName = "CPUUsageStream";
    private static final String streamVersion = "1.0.0";
    private List<CPUStatistic> cpuStatistics;
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
    public CPUPublisher(DASConfiguration dasConfiguration) throws DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException, DataEndpointException, DataEndpointConfigurationException,
            TransportException {

        super(dasConfiguration);
        setDataStream(streamName, streamVersion);

        logger.info("Starting CPU Publisher; Host: " + dasConfiguration.getHost() + "\tThriftPort: "
                + dasConfiguration.getThriftPort() + "\tStreamID: " + streamName + ":" + streamVersion);

    }

    /**
     * Need to set CPU statistic data before publish data to DAS
     *
     * @param cpuStatistics
     * @param applicationId
     * @param timestamp
     */
    public void setCPUStatistic(List<CPUStatistic> cpuStatistics, String applicationId, long timestamp) {
        this.cpuStatistics = cpuStatistics;
        this.applicationId = applicationId;
        this.timestamp = timestamp;
    }

    /**
     * Publish CPU Load Log data to DAS
     */
    @Override
    public void publishEvents() {

        if (cpuStatistics != null && !cpuStatistics.isEmpty()) {
            CPUStatistic cpuStat = cpuStatistics.get(cpuStatistics.size() - 1);

            Event event = new Event(dataStream, System.currentTimeMillis(), null, null,
                    new Object[]{timestamp,
                            applicationId,
                            cpuStat.getProcessCPULoad(),
                            cpuStat.getSystemCPULoad()
                    });

            dataPublisher.publish(event);

            //check is debug enable
            if (logger.isDebugEnabled()) {

                StringBuilder cpuEvent = new StringBuilder();
                cpuEvent.append("publish CPU data : ");
                cpuEvent.append(timestamp);
                cpuEvent.append(" , ");
                cpuEvent.append(applicationId);
                cpuEvent.append(" , ");
                cpuEvent.append(cpuStat.getProcessCPULoad());
                cpuEvent.append(" , ");
                cpuEvent.append(cpuStat.getSystemCPULoad());

                logger.debug(cpuEvent.toString());
            }

        }

    }

}
