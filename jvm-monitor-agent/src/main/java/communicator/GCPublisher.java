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
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;


public class GCPublisher extends DASPublisher {

    /**
     * Set default Garbage collection log Stream
     * <p>
     * Data format must be in the following order in given types in "GarbageCollectionStream":-
     * <p>
     * String	GC_TYPE
     * long     GC_DURATION
     * long     GC_START_TIME
     * String	GC_CAUSE
     * long     EDEN_USED_MEMORY_AFTER_GC
     * long     EDEN_USED_MEMORY_BEFORE_GC
     * long	    SURVIVOR_USED_MEMORY_AFTER_GC
     * long	    SURVIVOR_USED_MEMORY_BEFORE_GC
     * long	    OLD_GEN_USED_MEMORY_AFTER_GC
     * long  	OLD_GEN_USED_MEMORY_BEFORE_GC
     * long 	EDEN_COMMITTED_MEMORY_AFTER_GC
     * long 	EDEN_COMMITTED_MEMORY_BEFORE_GC
     * long 	SURVIVOR_COMMITTED_MEMORY_AFTER_GC
     * long 	SURVIVOR_COMMITTED_MEMORY_BEFORE_GC
     * long 	OLD_GEN_COMMITTED_MEMORY_AFTER_GC
     * long 	OLD_GEN_COMMITTED_MEMORY_BEFORE_GC
     * long 	EDEN_MAX_MEMORY_AFTER_GC
     * long 	EDEN_MAX_MEMORY_BEFORE_GC
     * long 	SURVIVOR_MAX_MEMORY_AFTER_GC
     * long 	SURVIVOR_MAX_MEMORY_BEFORE_GC
     * long 	OLD_GEN_MAX_MEMORY_AFTER_GC
     * long 	OLD_GEN_MAX_MEMORY_BEFORE_GC
     */
    private static final String STREAM_NAME = "GarbageCollectionStream";
    private static final String STREAM_VERSION = "1.0.0";

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
    public GCPublisher(String hostname, int defaultThriftPort, String username, String password) throws
            SocketException,
            UnknownHostException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointException,
            DataEndpointConfigurationException {

        super(hostname, defaultThriftPort, username, password, STREAM_NAME, STREAM_VERSION);

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
            eventAgent.publishLogEvents(dataPublisher, dataStream, appID, garbageCollectionLog.poll());
        }

    }


}
