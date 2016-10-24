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
     * "fileName" as java file name needed to monitor
     * "jarFileRelativePath" as file location relative to the project directory
     * "arg" as input arguments valid only for monitoring App
     * Input Options
     */
    private String fileName = "NormalApp3";
    private String fileExtension = ".java";
    private String jarFileRelativePath = "/jvm-monitor-agent/src/samples/applications/NormalApp3";
    private String arg = "";

    //Main program Input Options
    private String appName;
    private boolean isAbsolutePath = false;
    private boolean isJarFile = false;
    private boolean doCompile = true;
    private boolean doRecursion = false;
    private boolean killMultipleProcess = true;


    private String currentDir = System.getProperty("user.dir");
    private String jarFilePath = currentDir + jarFileRelativePath + "/";

    /**
     * set user argument
     *
     * @param args
     */
    private void setAegument(String args[]) {

        List<String> val = setOptions(args);

        if (val.size() >= 2) {

            fileName = val.get(0);

            if (isJarFile) {
                fileExtension = ".jar";
            }

            String temPath = val.get(1);
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

            if (val.size() > 2) {
                for (int x = 2; x < val.size(); x++) {
                    arg += val.get(x) + " ";
                }
                arg = arg.trim();
            }

        }

    }

    /**
     * set Options argument
     *
     * @param OptionsArgs
     * @return
     */
    private List setOptions(String OptionsArgs[]) {

        List<String> args = new ArrayList<String>();

        for (int x = 0; x < OptionsArgs.length; x++) {

            String s = OptionsArgs[x];
            if ("-j".equals(s)) {
                isJarFile = true;
            } else if ("-a".equals(s)) {
                isAbsolutePath = true;
            } else if ("-f".equals(s)) {
                doCompile = false;
            } else if ("-r".equals(s)) {
                doRecursion = true;
            } else if ("-x".equals(s)) {
                killMultipleProcess = false;
            } else {
                args.add(s);
            }

        }

        return args;

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

    private void setAppName() {
        arg = arg.trim();
        appName = fileName;
        if (!"".equals(arg)) {
            appName += " " + arg;
        }
    }

    /**
     * Start monitored application
     */
    private void runMonitoredApp() {

        killMultipleProcess(appName);

        String error, out = "";
        boolean isCompile = true;

        if (!isJarFile) {
            if ((new File(jarFilePath + fileName + ".class").isFile()) && !doCompile) {

                String cmd = "java -classpath " + jarFilePath + " " + fileName + " " + arg;
                logger.info(cmd);
                try {
                    Runtime.getRuntime().exec(cmd);
                } catch (Exception e) {
                    logger.error(e);
                }

            } else if ((new File(jarFilePath + fileName + fileExtension).isFile())) {

                String cmd = "javac " + jarFilePath + fileName + fileExtension;
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
                    cmd = "java -classpath " + jarFilePath + " " + fileName + " " + arg;
                    logger.info(cmd);
                    Runtime.getRuntime().exec(cmd);
                } catch (Exception e) {
                    logger.error(e);
                }

            } else {
                logger.error("Could not find \"" + fileName + fileExtension + "\" java file in given directory: " + jarFilePath);
                System.exit(0);
            }

        } else {

            if (!(new File(jarFilePath + fileName + fileExtension).isFile())) {
                System.err.println("Could not find .jar file in given directory: " + jarFilePath + fileName + fileExtension);
                System.exit(0);
            }

            try {
                Runtime.getRuntime().exec("java -jar " + jarFilePath + fileName + fileExtension);
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

    private String getPID() {

        String pid = null;

        for (VirtualMachineDescriptor vmd : VirtualMachine.list()) {
            if (appName.equals(vmd.displayName())) {
                pid = vmd.id();
                logger.info("PID found. PID: " + vmd.id() + "\tName: " + vmd.displayName());
            }
        }

        return pid;
    }

    private void startSendMonitoredData() throws MalformedObjectNameException,
            InterruptedException,
            IOException,
            AttachNotSupportedException,
            MonitoringNotStartedException,
            DataEndpointException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointConfigurationException {

        runMonitoredApp();
        String pid = getPID();

        if (pid == null) {
            logger.error("Given \"" + fileName + "\" file is not running");
        } else {

            String appID = (fileName).trim();

            Controller controllerObj = new Controller();
            controllerObj.sendUsageData(pid, appID, controllerObj);

            try {
                killProcess(pid);
            } catch (Exception e) {
                logger.error(e);
            }
        }

    }

    /**
     * This function will be recursion if user give "-r" as a option.
     */
    private void runMonitor() {

        try {
            startSendMonitoredData();
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

    public static void main(String[] args) {

        JVMMonitorAgent jvmMonitor = new JVMMonitorAgent();

        try {
            if (args.length >= 2) {
                jvmMonitor.setAegument(args);
            }
        } catch (Exception e) {
            logger.error(e);
        }

        jvmMonitor.setAppName();
        jvmMonitor.runMonitor();

    }


}
