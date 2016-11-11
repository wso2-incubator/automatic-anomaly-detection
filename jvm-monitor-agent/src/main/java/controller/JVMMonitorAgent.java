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


public class JVMMonitorAgent {

    final static Logger logger = Logger.getLogger(JVMMonitorAgent.class);

    //Main program Input Options
    private String appName;
    private String pid;

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
     * This function will be recursion if user give "-r" as a option.
     */
    private void runMonitor() throws AgentLoadException,
            AgentInitializationException {

        try {
            startSendingMonitoredData();
        } catch (UndeclaredThrowableException e) {


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
