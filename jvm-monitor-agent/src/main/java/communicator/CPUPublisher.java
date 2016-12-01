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

import jvmmonitor.exceptions.UnknownMonitorAgentTypeException;
import jvmmonitor.management.MonitorType;
import jvmmonitor.models.CPUStatistic;
import jvmmonitor.models.UsageMonitorLog;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

import java.net.SocketException;
import java.net.UnknownHostException;


public class CPUPublisher extends DASPublisher implements Runnable {

    /**
     * Set default CPU usage stream
     * <p>
     * Data format must be in the following order in given types in "CPUUsageStream":-
     * <p>
     * long      timeStamp
     * String    appID
     * double    processCPULoad
     * double    systemCPULoad
     */
    private static final String streamName = "CPUUsageStream";
    private static final String streamVersion = "1.0.0";
    private UsageMonitorLog usageLogObj;

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
    public CPUPublisher(String hostname, int defaultThriftPort, String username, String password) throws
            SocketException,
            UnknownHostException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointException,
            DataEndpointConfigurationException {

        super(hostname, defaultThriftPort, username, password, streamName, streamVersion);

    }

    /**
     * Need to set UsageMonitorLog before publish data to DAS
     *
     * @param usageLogObj
     */
    public void setUsageLogObj(UsageMonitorLog usageLogObj) {
        this.usageLogObj = usageLogObj;
    }

    @Override
    public void run() {

        try {
            //Send data to EventPublisher
            try {
                eventAgent.publishLogEvents(dataPublisher, dataStream, usageLogObj.getTimeStamp(), appID, (CPUStatistic) usageLogObj.getUsageLog(MonitorType.CPU_USAGE_MONITOR.getValue()));
            } catch (UnknownMonitorAgentTypeException e) {
                e.printStackTrace();
            }
        } catch (DataEndpointConfigurationException e) {
            e.printStackTrace();
        } catch (DataEndpointAgentConfigurationException e) {
            e.printStackTrace();
        } catch (DataEndpointException e) {
            e.printStackTrace();
        } catch (TransportException e) {
            e.printStackTrace();
        } catch (DataEndpointAuthenticationException e) {
            e.printStackTrace();
        }

    }

}
