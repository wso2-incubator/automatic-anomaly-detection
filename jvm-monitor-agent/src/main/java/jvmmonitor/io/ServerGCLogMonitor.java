package jvmmonitor.io;

/*
*  Copyright (c) ${YEAR}, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import communicator.DASPublisher;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

import java.io.FileNotFoundException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ServerGCLogMonitor {

    private static final String SAMPLE_LOG_PATH = System.getProperty("user.dir") + "/gc.log.2.current";

    public static void main(String[] args) throws DataEndpointException,
            SocketException,
            UnknownHostException,
            FileNotFoundException,
            DataEndpointConfigurationException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException {

        DASPublisher dasPublisherObj = new DASPublisher();
        dasPublisherObj.publishXXgcLogData(SAMPLE_LOG_PATH);

    }
}
