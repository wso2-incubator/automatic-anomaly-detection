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

import jvmmonitor.model.GarbageCollectionLog;
import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

import java.io.FileNotFoundException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;


public class DASgcPublisher extends DASPublisher {

    final static Logger logger = Logger.getLogger(DASgcPublisher.class);

    /**
     * Need to set client-truststore.jks file located path
     *
     * @param defaultThriftPort
     * @param defaultBinaryPort
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
    public DASgcPublisher(int defaultThriftPort, int defaultBinaryPort, String username, String password) throws
            SocketException,
            UnknownHostException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointException,
            DataEndpointConfigurationException {

        super(defaultThriftPort, defaultBinaryPort, username, password);

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
        String HTTPD_LOG_STREAM = "GarbageCollectionStream";
        String VERSION = "1.0.0";
        setDataStream(HTTPD_LOG_STREAM, VERSION);

        /**
         * For Garbage Collection Log data
         * Garbage Collection data should be in format -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps
         * <p>
         * Data format must be in the following order in given types in "gcLogStream":-
         * <p>
         * String   fileID
         * String	Date
         * String   TimeStarted
         * double   TimePass
         * String   GCFlage
         * String   CaseCollection
         * String   GCName
         * long     YoungGenerationBefore
         * long     YoungGenerationAfter
         * long     TotalYoungGeneration
         * long     OldGenerationBefore
         * long     OldGenerationAfter
         * long     TotalOldGeneration
         * long     MetaspaceGenerationBefore
         * long     MetaspaceGenerationAfter
         * long     TotalMetaspaceGeneration
         * long     TotalUsedHeapBefore
         * long     TotalUsedHeapAfter
         * long     TotalAvailableHeap
         * double   GCEventDuration
         * double   GCEventUserTimes
         * double   GCEventSysTimes
         * double   GCEventRealTimes
         */
//        String HTTPD_LOG_STREAM = "GCLogStream";
//        String VERSION = "1.0.0";
//        setDataStream(HTTPD_LOG_STREAM, VERSION);

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

        //HTTPD_LOG_STREAM = "GarbageCollectionStream"
        //VERSION = "1.0.0"

        while (!garbageCollectionLog.isEmpty()) {
            eventAgent.publishLogEvents(dataPublisher, dataStream, appID, garbageCollectionLog.poll());
        }

    }

    /**
     * Send data to EventPublisher
     *
     * @param fileName
     * @throws TransportException
     * @throws DataEndpointConfigurationException
     * @throws FileNotFoundException
     * @throws DataEndpointAuthenticationException
     * @throws DataEndpointException
     * @throws DataEndpointAgentConfigurationException
     */
    public void publishXXgcLogData(String fileName) throws TransportException,
            DataEndpointConfigurationException,
            FileNotFoundException,
            DataEndpointAuthenticationException,
            DataEndpointException,
            DataEndpointAgentConfigurationException {

        //HTTPD_LOG_STREAM = "gcLogStream"
        //VERSION = "1.0.0"

        eventAgent.publishLogEvents(dataPublisher, dataStream, fileName);

    }

}
