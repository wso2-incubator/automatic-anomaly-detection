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
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

import javax.management.MalformedObjectNameException;
import java.io.File;
import java.io.IOException;

/**
 * This class runs "BadCode.jar" file to get jvm usage data
 * Need to set .jar file path
 */
public class Test {

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

        //Set jar file name & file located path relative to project directory
        String fileName = "BadCode.jar";
        String jarFilePath = "/jvm-monitor-agent/src/samples";

        String currentDir = System.getProperty("user.dir");
        jarFilePath = currentDir + jarFilePath + "/" + fileName;

        if (!(new File(jarFilePath).isFile())) {
            System.err.println("Could not find .jar file in given directory: " + jarFilePath);
            System.exit(0);
        }

        Runtime.getRuntime().exec("java -jar " + jarFilePath);
        String pid = null;
        int counter = 0;

        for (VirtualMachineDescriptor vmd : VirtualMachine.list()) {
            if (vmd.displayName().indexOf(fileName) != -1) {
                pid = vmd.id();
                System.out.println(vmd.id() + "\t" + vmd.displayName());
                counter++;
            }
        }

        if (pid == null) {
            System.err.println("Given .jar file is not running");
        } else if (counter == 1) {
            Controller controllerObj = new Controller();
            controllerObj.sendUsageData(pid, controllerObj);
            Runtime.getRuntime().exec("kill -9 " + pid);
        } else {
            System.err.println("You have multiple " + fileName + " Process");
        }

    }
}
