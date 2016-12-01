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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 *
 */
public class DASConfigurations {

    private final static Logger logger = Logger.getLogger(DASConfigurations.class);

    private final String host;
    private final int defaultThriftPort;
    private final int securePort;
    private final String username;
    private final String password;
    private final String dataAgentConfPath;
    private final String trustStorePath;
    private final String trustStorePassword;


    public DASConfigurations(String host, int defaultThriftPort, String username, String password, String resourcesFilePath, String trustStorePassword) {

        if (host == "localhost") {
            try {
                host = getLocalAddress();
            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
            }
        }

        this.host = host;
        this.defaultThriftPort = defaultThriftPort;
        this.securePort = defaultThriftPort + 100;
        this.username = username;
        this.password = password;
        this.dataAgentConfPath = resourcesFilePath;
        this.trustStorePath = resourcesFilePath;
        this.trustStorePassword = trustStorePassword;

    }

    public DASConfigurations(String host, int defaultThriftPort, int securePort, String username, String password, String resourcesFilePath, String trustStorePassword) {

        if (host == "localhost") {
            try {
                host = getLocalAddress();
            } catch (SocketException | UnknownHostException e) {
                logger.error(e.getMessage(), e);
            }
        }

        this.host = host;
        this.defaultThriftPort = defaultThriftPort;
        this.securePort = securePort;
        this.username = username;
        this.password = password;
        this.dataAgentConfPath = resourcesFilePath;
        this.trustStorePath = resourcesFilePath;
        this.trustStorePassword = trustStorePassword;

    }

    public DASConfigurations(String host, int defaultThriftPort, int securePort, String username, String password, String dataAgentConfPath, String trustStorePath, String trustStorePassword) {

        if (host == "localhost") {
            try {
                host = getLocalAddress();
            } catch (SocketException | UnknownHostException e) {
                logger.error(e.getMessage(), e);
            }
        }

        this.host = host;
        this.defaultThriftPort = defaultThriftPort;
        this.securePort = securePort;
        this.username = username;
        this.password = password;
        this.dataAgentConfPath = dataAgentConfPath;
        this.trustStorePath = trustStorePath;
        this.trustStorePassword = trustStorePassword;

    }


    public String getHost() {
        return host;
    }

    public int getDefaultThriftPort() {
        return defaultThriftPort;
    }

    public int getSecurePort() {
        return securePort;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDataAgentConfPath() {
        return dataAgentConfPath;
    }

    public String getTrustStorePath() {
        return trustStorePath;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    private String getLocalAddress() throws SocketException, UnknownHostException {
        Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
        while (ifaces.hasMoreElements()) {
            NetworkInterface iface = ifaces.nextElement();
            Enumeration<InetAddress> addresses = iface.getInetAddresses();

            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                    return addr.toString().replace("/", "");
                }
            }
        }
        return InetAddress.getLocalHost().toString().replace("/", "");
    }

}
