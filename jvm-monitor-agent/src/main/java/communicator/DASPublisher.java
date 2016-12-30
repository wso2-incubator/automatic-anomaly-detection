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

/**
 * This is set data-agent-configurations, create DataPublisher instance
 */
public class DASPublisher {

    private final static Logger logger = Logger.getLogger(DASPublisher.class);

    DataPublisher dataPublisher;
    String dataStream;

    /**
     * Constructor
     *
     * @param dasConfiguration
     * @throws DataEndpointAuthenticationException
     * @throws DataEndpointAgentConfigurationException
     * @throws TransportException
     * @throws DataEndpointException
     * @throws DataEndpointConfigurationException
     */
    public DASPublisher(DASConfiguration dasConfiguration)
            throws DataEndpointAuthenticationException, DataEndpointAgentConfigurationException, TransportException,
            DataEndpointException, DataEndpointConfigurationException {

        setDataAgentConfigurations(dasConfiguration.getDataAgentConfPath(), dasConfiguration.getTrustStorePath(),
                dasConfiguration.getTrustStorePassword());

        String type = "Thrift";
        String url = "tcp://" + dasConfiguration.getHost() + ":" + dasConfiguration.getThriftPort();
        String authURL = "ssl://" + dasConfiguration.getHost() + ":" + dasConfiguration.getSecurePort();

        dataPublisher = new DataPublisher(type, url, authURL, dasConfiguration.getUsername(),
                dasConfiguration.getPassword());

    }

    /**
     * Generate Stream ID
     *
     * @param streamName
     * @param streamVersion
     */
    protected void setDataStream(String streamName, String streamVersion) {
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

    private void setDataAgentConfigurations(String dataAgentConfPath, String trustStorePath,
                                            String trustStorePassword) {

        // Set data-agent-conf.xml file path
        File dataAgentFilePath = new File(dataAgentConfPath + File.separator + "data-agent-conf.xml");

        if (!dataAgentFilePath.exists()) {
            dataAgentFilePath = new File("resources" + File.separator + "data-agent-conf.xml");
        }
        if (!dataAgentFilePath.exists()) {
            dataAgentFilePath = new File("test" + File.separator + "resources" + "data-agent-conf.xml");
        }
        if (!dataAgentFilePath.exists()) {
            logger.error("data-agent-conf.xml File not found in : " + dataAgentFilePath.getAbsolutePath());
        } else {
            AgentHolder.setConfigPath(dataAgentFilePath.getAbsolutePath());
        }

        // Set the client-truststore.jks file located path and trustStorePassword
        File trustStoreFilePath = new File(trustStorePath + File.separator + "client-truststore.jks");

        if (!trustStoreFilePath.exists()) {
            trustStoreFilePath = new File("resources" + File.separator + "client-truststore.jks");
        }
        if (!trustStoreFilePath.exists()) {
            trustStoreFilePath = new File("test" + File.separator + "resources" + "client-truststore.jks");
        }
        if (!trustStoreFilePath.exists()) {
            logger.error("client-truststore.jks File not found in : " + trustStoreFilePath.getAbsolutePath());
        } else {
            try {
                System.setProperty("javax.net.ssl.trustStore", trustStoreFilePath.getAbsolutePath());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
        }

    }

}
