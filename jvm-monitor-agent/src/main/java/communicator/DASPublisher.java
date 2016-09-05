package communicator;

import jvmmonitor.model.CPULoadLog;
import jvmmonitor.model.GarbageCollectionLog;
import jvmmonitor.model.MemoryUsageLog;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

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

public class DASPublisher {

    public void publishMemoryData(long date, MemoryUsageLog memoryUsageLog) throws SocketException,
            UnknownHostException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            DataEndpointException,
            DataEndpointConfigurationException,
            TransportException {

        String HTTPD_LOG_STREAM="memoryStream";
        String VERSION="1.0.0";
        int defaultThriftPort=7611;
        int defaultBinaryPort=9611;
        HttpdAgent agent = new HttpdAgent(HTTPD_LOG_STREAM, VERSION, defaultThriftPort, defaultBinaryPort);

        agent.initialize();
        agent.publishLogEvents(date, memoryUsageLog);


    }

    public void publishGCData(List<GarbageCollectionLog> garbageCollectionLog) throws SocketException,
            UnknownHostException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            DataEndpointException,
            DataEndpointConfigurationException,
            TransportException {

        String HTTPD_LOG_STREAM="gcStream";
        String VERSION="1.0.0";
        int defaultThriftPort=7611;
        int defaultBinaryPort=9611;
        HttpdAgent agent = new HttpdAgent(HTTPD_LOG_STREAM, VERSION, defaultThriftPort, defaultBinaryPort);

        agent.initialize();
        for (GarbageCollectionLog gc:garbageCollectionLog) {
            agent.publishLogEvents(gc);
        }

    }

    public void publishCPUData(long date, CPULoadLog cpuLoadLog) throws SocketException,
            UnknownHostException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            DataEndpointException,
            DataEndpointConfigurationException,
            TransportException {

        String HTTPD_LOG_STREAM="cpuStream";
        String VERSION="1.0.0";
        int defaultThriftPort=7611;
        int defaultBinaryPort=9611;
        HttpdAgent agent = new HttpdAgent(HTTPD_LOG_STREAM, VERSION, defaultThriftPort, defaultBinaryPort);

        agent.initialize();
        agent.publishLogEvents(date, cpuLoadLog);

    }


}
