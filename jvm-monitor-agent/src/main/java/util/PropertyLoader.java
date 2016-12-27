package util;

import exceptions.PropertyCannotBeLoadedException;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

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

/**
 * Load the properties from jma.properties to configure JVM monitor agent
 */
public class PropertyLoader {

    private final static String PROPERTY_FILE = "jma.properties";
    private final static Logger logger = Logger.getLogger(PropertyLoader.class);

    //das publisher configurations
    public static String dasAddress;
    public static int dasThriftPort;
    public static int dasSecurePort;
    public static String dataAgentConfPath;
    public static String trustStorePath;
    public static String trustStorePassword;

    public static String dasUsername;
    public static String dasPassword;

    //targeted monitoring mode
    public static String mode;

    //targeted remote server configuration
    public static String targetAddress;
    public static String targetRmiServerPort;
    public static String targetRmiRegistryPort;
    public static String targetUsername;
    public static String targetPassword;

    //targeted remote server snmp configurations
    public static String snmpAddress;
    public static String snmpPort;

    //monitor running app using PID configurations
    public static String pid;

    /**
     * Load JVM monitor agent properties form the jma.properties file
     *
     * @throws PropertyCannotBeLoadedException
     */
    public static void loadProperties() throws PropertyCannotBeLoadedException {

        Properties prop = new Properties();
        InputStream input;

        try {
            input = new FileInputStream("bin"+ File.separator + PROPERTY_FILE); //load properties from the bin file when deployed
        } catch (FileNotFoundException e1) {
            //load properties at development environments
            input = PropertyLoader.class.getClassLoader().getResourceAsStream(PROPERTY_FILE);
        }

        try {
            if (input != null) {

                prop.load(input);

                dasAddress = prop.getProperty("jma.das.address");
                dasThriftPort = Integer.parseInt(prop.getProperty("jma.das.thrift_port"));
                dasSecurePort = Integer.parseInt(prop.getProperty("jma.das.secure_port"));
                dataAgentConfPath = prop.getProperty("jma.das.data_agent_conf.path");
                trustStorePath = prop.getProperty("javax.net.ssl.trust_store.path");
                trustStorePassword = prop.getProperty("javax.net.ssl.trust_store.password");

                dasUsername = prop.getProperty("jma.das.username");
                dasPassword = prop.getProperty("jma.das.password");

                mode = prop.getProperty("jma.target.mode");

                targetAddress = prop.getProperty("jma.target.address");
                targetRmiServerPort = prop.getProperty("jma.target.rmi_server_port");
                targetRmiRegistryPort = prop.getProperty("jma.target.rmi_registry_port");
                targetUsername = prop.getProperty("jma.target.username");
                targetPassword = prop.getProperty("jma.target.password");

                pid = prop.getProperty("jma.target.pid");

                snmpAddress = prop.getProperty("jma.target.snmp.address");
                snmpPort = prop.getProperty("jma.target.snmp.port");

                input.close();

            } else {
                String msg = "The property file can not be loaded";
                logger.error(msg);
                throw new PropertyCannotBeLoadedException(msg);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new PropertyCannotBeLoadedException(e.getMessage(), e);
        }
    }
}
