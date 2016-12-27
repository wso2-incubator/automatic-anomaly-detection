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
package util;

import exceptions.PropertyCannotBeLoadedException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Load the properties from jma.properties to configure JVM monitor agent
 */
public class JmaProperties {

    private final static String PROPERTY_FILE = "jma.properties";
    private final static Logger logger = Logger.getLogger(JmaProperties.class);

    // das publisher configurations
    private String dasAddress;
    private int dasThriftPort;
    private int dasSecurePort;
    private String dataAgentConfPath;
    private String trustStorePath;
    private String trustStorePassword;

    private String dasUsername;
    private String dasPassword;

    // targeted monitoring mode
    private String mode;

    // targeted remote server configuration
    private String targetAddress;
    private String targetRmiServerPort;
    private String targetRmiRegistryPort;
    private String targetUsername;
    private String targetPassword;

    // targeted remote server snmp configurations
    private String snmpAddress;
    private String snmpPort;

    // monitor running app using PID configurations
    private String pid;

    /**
     * JmaProperties constructor
     * Load property attributes
     */
    public JmaProperties() throws PropertyCannotBeLoadedException {
        init();// load property attributes
        logger.info("Properties loaded successfully");

    }

    /**
     * Load JVM monitor agent properties form the jma.properties file
     *
     * @throws PropertyCannotBeLoadedException
     */
    private void init() throws PropertyCannotBeLoadedException {

        Properties properties = new Properties();
        InputStream input;

        try {
            input = new FileInputStream("bin" + File.separator + PROPERTY_FILE); // load properties from the bin file
            // when deployed
        } catch (FileNotFoundException e1) {
            // load properties at development environments
            input = JmaProperties.class.getClassLoader().getResourceAsStream(PROPERTY_FILE);
        }
        try {
            if (input != null) {
                properties.load(input);

                dasAddress = properties.getProperty("jma.das.address");
                dasThriftPort = Integer.parseInt(properties.getProperty("jma.das.thrift_port"));
                dasSecurePort = Integer.parseInt(properties.getProperty("jma.das.secure_port"));
                dataAgentConfPath = properties.getProperty("jma.das.data_agent_conf.path");
                trustStorePath = properties.getProperty("javax.net.ssl.trust_store.path");
                trustStorePassword = properties.getProperty("javax.net.ssl.trust_store.password");

                dasUsername = properties.getProperty("jma.das.username");
                dasPassword = properties.getProperty("jma.das.password");

                mode = properties.getProperty("jma.target.mode");

                targetAddress = properties.getProperty("jma.target.address");
                targetRmiServerPort = properties.getProperty("jma.target.rmi_server_port");
                targetRmiRegistryPort = properties.getProperty("jma.target.rmi_registry_port");
                targetUsername = properties.getProperty("jma.target.username");
                targetPassword = properties.getProperty("jma.target.password");

                pid = properties.getProperty("jma.target.pid");

                snmpAddress = properties.getProperty("jma.target.snmp.address");
                snmpPort = properties.getProperty("jma.target.snmp.port");

            } else {
                String msg = "The property file can not be loaded";
                throw new PropertyCannotBeLoadedException(msg);
            }
        } catch (IOException e) {
            throw new PropertyCannotBeLoadedException(e.getMessage(), e);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public String getDasAddress() {
        return dasAddress;
    }

    public int getDasThriftPort() {
        return dasThriftPort;
    }

    public int getDasSecurePort() {
        return dasSecurePort;
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

    public String getDasUsername() {
        return dasUsername;
    }

    public String getDasPassword() {
        return dasPassword;
    }

    public String getMode() {
        return mode;
    }

    public String getTargetAddress() {
        return targetAddress;
    }

    public String getTargetRmiServerPort() {
        return targetRmiServerPort;
    }

    public String getTargetRmiRegistryPort() {
        return targetRmiRegistryPort;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public String getTargetPassword() {
        return targetPassword;
    }

    public String getSnmpAddress() {
        return snmpAddress;
    }

    public String getSnmpPort() {
        return snmpPort;
    }

    public String getPid() {
        return pid;
    }
}
