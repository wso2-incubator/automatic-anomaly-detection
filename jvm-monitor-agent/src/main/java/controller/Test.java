package controller;
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

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import jvmmonitor.exceptions.MonitoringNotStartedException;
import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

import javax.management.MalformedObjectNameException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class runs "BadCode.jar" file to get jvm usage data
 * Need to set .java or .jar file path
 */
public class Test {

    final static Logger logger = Logger.getLogger(Test.class);

    public static void main(String[] args) throws IOException,
            AttachNotSupportedException,
            MalformedObjectNameException,
            InterruptedException,
            MonitoringNotStartedException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointException,
            DataEndpointConfigurationException {

        //If you use java code
        //Set java file name & file located path relative to project directory
        String fileName = "BadCode";
        String jarFilePath = "/jvm-monitor-agent/src/samples/applications";

        String currentDir = System.getProperty("user.dir");
        jarFilePath = currentDir + jarFilePath + "/";
        String error = null, out = "";
        boolean isCompile = true;

        if ((new File(jarFilePath + fileName + ".class").isFile())) {

            String cmd = "java -classpath " + jarFilePath + " " + fileName;
            logger.info(cmd);
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (Exception e) {
                logger.error(e);
            }

        } else if ((new File(jarFilePath + fileName + ".java").isFile())) {

            String cmd = "javac " + jarFilePath + fileName + ".java";
            logger.info(cmd);

            try {
                Process prc = Runtime.getRuntime().exec(cmd);
                BufferedReader stdError = new BufferedReader(new InputStreamReader(prc.getErrorStream()));
                while ((error = stdError.readLine()) != null) {
                    out += error + '\n';
                    isCompile = false;
                }

                if (!isCompile) {
                    logger.error(out);
                    System.exit(0);
                }

                Thread.sleep(3000);
                cmd = "java -classpath " + jarFilePath + " " + fileName;
                logger.info(cmd);
                Runtime.getRuntime().exec(cmd);
            } catch (Exception e) {
                logger.error(e);
            }

        } else {
            logger.error("Could not find \"" + fileName + "\" java file in given directory: " + jarFilePath);
            System.exit(0);
        }

        /*
        //If you use .jar file pleases uncomment following part and comment above part
        //Set jar file name & file located path relative to project directory
        String fileName = "BadCode.jar";
        String jarFilePath = "/jvm-monitor-agent/src/samples";

        String currentDir = System.getProperty("user.dir");
        jarFilePath = currentDir + jarFilePath + "/" + fileName;

        if (!(new File(jarFilePath).isFile())) {
            System.err.println("Could not find .jar file in given directory: " + jarFilePath);
            System.exit(0);
        }

        try {
            Runtime.getRuntime().exec("java -jar " + jarFilePath);
        } catch (Exception e) {
            logger.error(e);
        }
        */

        String pid = null;
        int counter = 0;

        for (VirtualMachineDescriptor vmd : VirtualMachine.list()) {
            if (vmd.displayName().indexOf(fileName) != -1) {
                pid = vmd.id();
                logger.info(vmd.id() + "\t" + vmd.displayName());
                counter++;
            }
        }

        if (pid == null) {
            logger.error("Given \"" + fileName + "\" file is not running");
        } else if (counter == 1) {
            Controller controllerObj = new Controller();
            controllerObj.sendUsageData(pid, controllerObj);
            try {
                Runtime.getRuntime().exec("kill -9 " + pid);
            } catch (Exception e) {
                logger.error(e);
            }
        } else {
            logger.error("You have multiple \"" + fileName + "\" Process");
        }

    }
}
