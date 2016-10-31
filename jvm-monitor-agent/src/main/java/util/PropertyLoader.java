package util;


import exceptions.PropertyCannotBeloadedException;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
public class PropertyLoader {

    private final static String propertyFile = "jma.properties";
    private final static Logger logger = Logger.getLogger(PropertyLoader.class);

    //das publisher configurations
    public static String DAS_ADDRESS;
    public static int DAS_THRIFT_PORT;
    public static int DAS_BINARY_PORT;
    public static String DAS_USERNAME;
    public static String DAS_PASSWORD;

    //targeted remote server configuration
    public static boolean REMOTE_MONITORING;
    public static String TARGET_ADDRESS;
    public static String TARGET_RMI_SERVER_PORT;
    public static String TARGET_RMI_REGISTRY_PORT;
    public static String TARGET_USERNAME;
    public static String TARGET_PASSWORD;

    //monitor running app using PID configurations
    public static boolean IS_PID;
    public static String PID;

    //monitor running app using app name(display name) configurations
    public static boolean IS_APP_NAME;
    public static String APP_NAME;

    //file execution configurations
    public static String FILE_NAME;
    public static String FILE_PATH;
    public static String FILE_ARGS;
    public static boolean IS_JAR;
    public static boolean IS_ABSOLUTE;
    public static boolean DO_COMPILE;
    public static boolean RE_RUN;
    public static boolean KILL_MULTIPLE;


    public static void loadProperties() throws PropertyCannotBeloadedException {


        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("bin/"+propertyFile);
        } catch (FileNotFoundException e1) {

            input = PropertyLoader.class.getClassLoader().getResourceAsStream(propertyFile);

        } finally {

            try {
                if (input != null) {

                    prop.load(input);

                    DAS_ADDRESS = prop.getProperty("jma.das.address");
                    DAS_THRIFT_PORT = Integer.parseInt(prop.getProperty("jma.das.thriftport"));
                    DAS_BINARY_PORT = Integer.parseInt(prop.getProperty("jma.das.binaryport"));
                    DAS_USERNAME = prop.getProperty("jma.das.username");
                    DAS_PASSWORD = prop.getProperty("jma.das.password");

                    REMOTE_MONITORING = Boolean.parseBoolean(prop.getProperty("jma.target.remote_monitoring"));
                    TARGET_ADDRESS = prop.getProperty("jma.target.address");
                    TARGET_RMI_SERVER_PORT = prop.getProperty("jma.target.rmi_server_port");
                    TARGET_RMI_REGISTRY_PORT = prop.getProperty("jma.target.rmi_registry_port");
                    TARGET_USERNAME = prop.getProperty("jma.target.username");
                    TARGET_PASSWORD = prop.getProperty("jma.target.password");

                    IS_PID = Boolean.parseBoolean(prop.getProperty("jma.target.is_pid"));
                    PID = prop.getProperty("jma.target.pid");

                    IS_APP_NAME = Boolean.parseBoolean(prop.getProperty("jma.target.is_app_name"));
                    APP_NAME = prop.getProperty("jma.target.app_name");

                    FILE_NAME = prop.getProperty("jma.target.file.name");
                    FILE_PATH = prop.getProperty("jma.target.file.path");
                    FILE_ARGS = prop.getProperty("jma.target.file.args");
                    IS_JAR = Boolean.parseBoolean(prop.getProperty("jma.target.file.is_jar"));
                    IS_ABSOLUTE = Boolean.parseBoolean(prop.getProperty("jma.target.file.is_absolute"));
                    DO_COMPILE = Boolean.parseBoolean(prop.getProperty("jma.target.file.do_compile"));
                    RE_RUN = Boolean.parseBoolean(prop.getProperty("jma.target.file.re_run"));
                    KILL_MULTIPLE = Boolean.parseBoolean(prop.getProperty("jma.target.file.kill_multi_processes"));
                    input.close();

                }else{
                    logger.error("Loading Properties failed : The property file can not be loaded");
                    throw new PropertyCannotBeloadedException();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
}
