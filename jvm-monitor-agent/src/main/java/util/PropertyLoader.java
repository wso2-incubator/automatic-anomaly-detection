package util;

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

    public static String DAS_ADDRESS;
    public static String DAS_THRIFT_PORT;
    public static String DAS_BINARY_PORT;
    public static String DAS_USERNAME;
    public static String DAS_PASSWORD;

    public static String TARGET_ADDRESS;
    public static String TARGET_RMI_SERVER_PORT;
    public static String TARGET_RMI_REGISTRY_PORT;
    public static String TARGET_USERNAME;
    public static String TARGET_PASSWORD;


    public static void loadProperties(){

        Properties prop =  new Properties();
        InputStream input = null;

        try {

            input = PropertyLoader.class.getClassLoader().getResourceAsStream(propertyFile);

            if (input != null){
                prop.load(input);
            }

            DAS_ADDRESS = prop.getProperty("jma.das.address");
            DAS_THRIFT_PORT= prop.getProperty("jma.das.thriftport");
            DAS_BINARY_PORT = prop.getProperty("jma.das.binaryport");
            DAS_USERNAME = prop.getProperty("jma.das.username");
            DAS_PASSWORD =prop.getProperty("jma.das.password");

            TARGET_ADDRESS = prop.getProperty("jma.target.address");
            TARGET_RMI_SERVER_PORT = prop.getProperty("jma.target.rmi_server_port");
            TARGET_RMI_REGISTRY_PORT = prop.getProperty("jma.target.rmi_registry_port");
            TARGET_USERNAME = prop.getProperty("jma.target.username");
            TARGET_PASSWORD = prop.getProperty("jma.target.password");


        } catch (IOException e){
            e.printStackTrace();
            System.exit(0);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
