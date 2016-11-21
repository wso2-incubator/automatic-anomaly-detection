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

import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.AgentHolder;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import org.wso2.carbon.databridge.commons.utils.DataBridgeCommonsUtils;

import java.io.File;
import java.net.SocketException;
import java.net.UnknownHostException;


public class DASPublisher {

    private final static Logger logger = Logger.getLogger(DASPublisher.class);

    DataPublisher dataPublisher;
    String dataStream;
    EventPublisher eventAgent;
    String appID = "";

    /**
     * Constructor
     *
     * @param host
     * @param defaultThriftPort
     * @param username
     * @param password
     * @param streamName
     * @param streamVersion
     * @throws SocketException
     * @throws UnknownHostException
     * @throws DataEndpointAuthenticationException
     * @throws DataEndpointAgentConfigurationException
     * @throws TransportException
     * @throws DataEndpointException
     * @throws DataEndpointConfigurationException
     */
    public DASPublisher(String host, int defaultThriftPort, String username, String password, String streamName, String streamVersion) throws
            SocketException,
            UnknownHostException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointException,
            DataEndpointConfigurationException {

        logger.info("Starting DAS HttpLog Agent");

        AgentHolder.setConfigPath(getDataAgentConfigPath());
        String type = getProperty("type", "Thrift");
        int receiverPort = defaultThriftPort;
        int securePort = receiverPort + 100;

        String url = getProperty("url", "tcp://" + host + ":" + receiverPort);
        String authURL = getProperty("authURL", "ssl://" + host + ":" + securePort);
        username = getProperty("username", username);
        password = getProperty("password", password);

        setDataStream(streamName, streamVersion);
        dataPublisher = new DataPublisher(type, url, authURL, username, password);
        eventAgent = new EventPublisher();

    }

    /**
     * Need to set this to identify particular application
     *
     * @param appID
     */
    public void setAppID(String appID) {
        this.appID = appID;
    }

    /**
     * Generate Stream ID
     *
     * @param streamName
     * @param streamVersion
     */
    private void setDataStream(String streamName, String streamVersion) {
        dataStream = DataBridgeCommonsUtils.generateStreamId(streamName, streamVersion);
    }

    /**
     * Shutdown the DataPublisher
     *
     * @throws DataEndpointException
     */
    public void shutdown() throws DataEndpointException {
        dataPublisher.shutdown();
    }

    /**
     * Need to set resource files located path
     *
     * @return Data agent config path
     */
    private static String getDataAgentConfigPath() {
        File filePath = new File("jvm-monitor-agent" + File.separator + "src" + File.separator + "main" + File.separator + "resources");
        if (!filePath.exists()) {
            filePath = new File("test" + File.separator + "resources");
        }
        if (!filePath.exists()) {
            filePath = new File("resources");
        }
        return filePath.getAbsolutePath() + File.separator + "data-agent-conf.xml";
    }

    /**
     * @param name
     * @param def
     * @return
     */
    private static String getProperty(String name, String def) {
        String result = System.getProperty(name);
        if (result == null || result.length() == 0 || result == "") {
            result = def;
        }
        return result;
    }

//    /**
//     * @return Local Host Address
//     * @throws SocketException
//     * @throws UnknownHostException
//     */
//    private static InetAddress getLocalAddress() throws SocketException, UnknownHostException {
//        Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
//        while (ifaces.hasMoreElements()) {
//            NetworkInterface iface = ifaces.nextElement();
//            Enumeration<InetAddress> addresses = iface.getInetAddresses();
//
//            while (addresses.hasMoreElements()) {
//                InetAddress addr = addresses.nextElement();
//                if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
//                    return addr;
//                }
//            }
//        }
//        return InetAddress.getLocalHost();
//    }

}
