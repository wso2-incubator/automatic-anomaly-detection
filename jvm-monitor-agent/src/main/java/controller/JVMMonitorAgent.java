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

package controller;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import exceptions.PropertyCannotBeloadedException;
import jvmmonitor.exceptions.MonitoringNotStartedException;
import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import util.PropertyLoader;

import javax.management.MalformedObjectNameException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class runs "BadCode.jar" file to get jvm usage data
 * Need to set .java or .jar file path
 */
public class JVMMonitorAgent {

    final static Logger logger = Logger.getLogger(JVMMonitorAgent.class);

    /**
     * If you want to change the default values; please set,
     * "appName" as java file name needed to monitor
     * "jarFileRelativePath" as file location relative to the project directory
     * "monitoredAppArgs" as input arguments valid only for monitoring App
     * Input Options
     */

    private String fileExtension;
    private String monitoredAppArgs;

    //Main program Input Options
    private String appName;
    private String pid;
    private boolean isAbsolutePath;
    private boolean isJarFile;
    private boolean doCompile;
    private boolean doRecursion;
    private boolean killMultipleProcess;
    private boolean remoteMonitoring;
    private boolean monitorWithPID;
    private boolean monitorWithAppName;


    private String currentDir;
    private String jarFilePath;

    public JVMMonitorAgent() {

        currentDir = System.getProperty("user.dir");
        jarFilePath = currentDir + "/";

        loadConfigs();

    }


    /**
     * This will kill multiple process if user give "-k" as a options
     *
     * @param appName
     */
    private void killMultipleProcess(String appName) {

        if (killMultipleProcess) {

            for (VirtualMachineDescriptor vmd : VirtualMachine.list()) {
                if (appName.equals(vmd.displayName())) {

                    try {
                        killProcess(vmd.id());
                        logger.info("kill PID : " + vmd.id() + " " + vmd.displayName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void killProcess(String pid) throws IOException {
        Runtime.getRuntime().exec("kill -9 " + pid);
    }


    /**
     * Start monitored application
     */
    private void runMonitoredApp() {

        killMultipleProcess(appName);

        String error, out = "";
        boolean isCompile = true;

        if (!isJarFile) {
            if ((new File(jarFilePath + appName + ".class").isFile()) && !doCompile) {

                String cmd = "java -classpath " + jarFilePath + appName + " " + monitoredAppArgs;
                logger.info(cmd);
                try {
                    Runtime.getRuntime().exec(cmd);
                } catch (Exception e) {
                    logger.error(e);
                }

            } else if ((new File(jarFilePath + appName + fileExtension).isFile())) {

                String cmd = "javac " + jarFilePath + appName + fileExtension;
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
                    cmd = "java -classpath " + jarFilePath + " " + appName + " " + monitoredAppArgs;
                    logger.info(cmd);
                    Runtime.getRuntime().exec(cmd);
                } catch (Exception e) {
                    logger.error(e);
                }

            } else {
                logger.error("Could not find \"" + appName + fileExtension + "\" java file in given directory: " + jarFilePath);
                System.exit(0);
            }

        } else {

            if (!(new File(jarFilePath + appName + fileExtension).isFile())) {
                System.err.println("Could not find .jar file in given directory: " + jarFilePath + appName + fileExtension);
                System.exit(0);
            }

            try {
                Runtime.getRuntime().exec("java -jar " + jarFilePath + appName + fileExtension);
            } catch (Exception e) {
                logger.error(e);
            }
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getPID(String appName) {

        String pid = null;

        for (VirtualMachineDescriptor vmd : VirtualMachine.list()) {
            if (appName != null && appName.equals(vmd.displayName())) {
                pid = vmd.id();
                logger.info("PID found. PID: " + vmd.id() + "\tName: " + vmd.displayName());
                break;
            }
        }
        return pid;
    }

    private String getAppName(String pid) {

        String appName = null;

        for (VirtualMachineDescriptor vmd : VirtualMachine.list()) {
            if (pid != null && pid.equals(vmd.id())) {
                appName = vmd.displayName();
                logger.info("AppName found. PID: " + vmd.id() + "\tName: " + vmd.displayName());
                break;
            }
        }
        return appName;
    }

    private void startSendingMonitoredData() throws MalformedObjectNameException,
            InterruptedException,
            IOException,
            AttachNotSupportedException,
            MonitoringNotStartedException,
            DataEndpointException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointConfigurationException,
            AgentLoadException,
            AgentInitializationException {

        Controller controller = new Controller();

        if (remoteMonitoring) { //if remote monitoring is enabled use JMX URL to connect
            controller.activateRemoteMonitoring();

        } else if (monitorWithPID) { //monitoring using PID is enabled start monitoring using the PID
            if (pid == null) {
                logger.error("No pid is given");
                System.exit(0);

            } else {
                appName = null;
                appName = getAppName(pid);
                if (appName == null) {
                    logger.error("No running application found with given PID : " + pid);
                } else {
                    controller.activateLocalMonitoring(pid, appName);
                }
            }
        } else { // start monitoring using the PID obtained by the Application name
            if (!monitorWithAppName) { //check if running app name is provided
                //run the application if the jar or java file is provided
                runMonitoredApp();
            }

            pid = null;
            pid = getPID(appName);
            if (pid == null) {
                logger.error("No running application found with given Application Display name : " + appName);
            } else {
                controller.activateLocalMonitoring(pid, appName);
            }

        }
    }

    /**
     * load configurations accordingly
     * Amount of configurations loaded is decided by the deciding parameters of the jma.properties
     */
    private void loadConfigs() {

        remoteMonitoring = PropertyLoader.REMOTE_MONITORING;
        monitorWithPID = PropertyLoader.IS_PID;
        monitorWithAppName = PropertyLoader.IS_APP_NAME;

        if (monitorWithPID) {
            this.pid = PropertyLoader.PID;

        } else if (monitorWithAppName) {
            this.appName = PropertyLoader.APP_NAME;

        } else {

            isAbsolutePath = PropertyLoader.IS_ABSOLUTE;
            isJarFile = PropertyLoader.IS_JAR;
            doCompile = PropertyLoader.DO_COMPILE;
            doRecursion = PropertyLoader.RE_RUN;
            killMultipleProcess = PropertyLoader.KILL_MULTIPLE;

            appName = PropertyLoader.FILE_NAME;


            if (isJarFile) {
                fileExtension = ".jar";
            } else {
                fileExtension = ".java";
            }

            String temPath = PropertyLoader.FILE_PATH;
            if ('/' != temPath.charAt(temPath.length() - 1)) {
                temPath = temPath + "/";
            }
            if ('/' != temPath.charAt(0)) {
                temPath = "/" + temPath;
            }

            if (isAbsolutePath) {
                jarFilePath = temPath;
            } else {
                jarFilePath = currentDir + temPath;
            }

            monitoredAppArgs = PropertyLoader.FILE_ARGS;
            if (monitoredAppArgs != null && monitoredAppArgs.trim().toLowerCase().equals("null")) {
                monitoredAppArgs = "";
            }

        }
    }


    /**
     * This function will be recursion if user give "-r" as a option.
     */
    private void runMonitor() throws AgentLoadException,
            AgentInitializationException {

        try {
            startSendingMonitoredData();
        } catch (UndeclaredThrowableException e) {

            if (doRecursion) {
                System.err.println("Monitored process stopped. monitoring app is now restarted");
                runMonitor();
            } else {
                System.err.println("Monitored process stopped. monitoring application is terminated");
                System.exit(0);
            }

        } catch (TransportException e) {
            e.printStackTrace();
        } catch (DataEndpointConfigurationException e) {
            e.printStackTrace();
        } catch (AttachNotSupportedException e) {
            e.printStackTrace();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (MonitoringNotStartedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (DataEndpointException e) {
            e.printStackTrace();
        } catch (DataEndpointAgentConfigurationException e) {
            e.printStackTrace();
        } catch (DataEndpointAuthenticationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) throws AgentLoadException,
            AgentInitializationException {

        try {
            PropertyLoader.loadProperties();
            logger.info("Properties loaded...");
            JVMMonitorAgent jvmMonitor = new JVMMonitorAgent();
            jvmMonitor.runMonitor();

        } catch (PropertyCannotBeloadedException e) {
            e.printStackTrace();
        }



    }


}
