package communicator;

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

import jvmmonitor.io.ExtractGCData;
import jvmmonitor.model.CPULoadLog;
import jvmmonitor.model.GarbageCollectionLog;
import jvmmonitor.model.MemoryUsageLog;
import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.AgentHolder;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import org.wso2.carbon.databridge.commons.utils.DataBridgeCommonsUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

public class HttpdAgent {

    private static String HTTPD_LOG_STREAM;
    private static String VERSION;
    private static int defaultThriftPort;
    private static int defaultBinaryPort;

    final static Logger logger = Logger.getLogger(HttpdAgent.class);

    private String type;
    private String url;
    private String authURL;
    private String username;
    private String password;

    public HttpdAgent(String HTTPD_LOG_STREAM, String VERSION, int defaultThriftPort, int defaultBinaryPort) {

        this.HTTPD_LOG_STREAM = HTTPD_LOG_STREAM;
        this.VERSION = VERSION;
        this.defaultThriftPort = defaultThriftPort;
        this.defaultBinaryPort = defaultBinaryPort;

    }

    public void initialize() throws SocketException, UnknownHostException {

        logger.info("Starting DAS HttpLog Agent");
        String currentDir = System.getProperty("user.dir");
        System.setProperty("javax.net.ssl.trustStore", currentDir + "/jvm-monitor-agent/src/main/resources/client-truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

        AgentHolder.setConfigPath(getDataAgentConfigPath());
        String host = getLocalAddress().getHostAddress();

        type = getProperty("type", "Thrift");
        int receiverPort = defaultThriftPort;
        if (type.equals("Binary")) {
            receiverPort = defaultBinaryPort;
        }
        int securePort = receiverPort + 100;

        url = getProperty("url", "tcp://" + host + ":" + receiverPort);
        authURL = getProperty("authURL", "ssl://" + host + ":" + securePort);
        username = getProperty("username", "admin");
        password = getProperty("password", "admin");

    }

    public void publishLogEvents(long date, CPULoadLog cpuLog) throws DataEndpointException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointConfigurationException {

        DataPublisher dataPublisher = new DataPublisher(type, url, authURL, username, password);
        String streamId = DataBridgeCommonsUtils.generateStreamId(HTTPD_LOG_STREAM, VERSION);

        Event event = new Event(streamId, date, null, null,
                new Object[]{cpuLog.getProcessCPULoad(), cpuLog.getSystemCPULoad()});
        dataPublisher.publish(event);

        dataPublisher.shutdown();

    }

    public void publishLogEvents(GarbageCollectionLog gcLog) throws DataEndpointException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointConfigurationException {

        DataPublisher dataPublisher = new DataPublisher(type, url, authURL, username, password);
        String streamId = DataBridgeCommonsUtils.generateStreamId(HTTPD_LOG_STREAM, VERSION);

        Event event = new Event(streamId, System.currentTimeMillis(), null, null,
                new Object[]{gcLog.getGcType(),
                        gcLog.getDuration(),
                        gcLog.getStartTime(),
                        gcLog.getGcCause(),
                        gcLog.getEdenUsedMemoryAfterGC(),
                        gcLog.getEdenUsedMemoryBeforeGC(),
                        gcLog.getSurvivorUsedMemoryAfterGC(),
                        gcLog.getSurvivorUsedMemoryBeforeGC(),
                        gcLog.getOldGenUsedMemoryAfterGC(),
                        gcLog.getOldGenUsedMemoryBeforeGC(),
                        gcLog.getEdenCommittedMemoryAfterGC(),
                        gcLog.getEdenCommittedMemoryBeforeGC(),
                        gcLog.getSurvivorCommittedMemoryAfterGC(),
                        gcLog.getSurvivorCommittedMemoryBeforeGC(),
                        gcLog.getOldGenCommittedMemoryAfterGC(),
                        gcLog.getOldGenCommittedMemoryBeforeGC(),
                        gcLog.getEdenMaxMemoryAfterGC(),
                        gcLog.getEdenMaxMemoryBeforeGC(),
                        gcLog.getSurvivorMaxMemoryAfterGC(),
                        gcLog.getSurvivorMaxMemoryBeforeGC(),
                        gcLog.getOldGenMaxMemoryAfterGC(),
                        gcLog.getOldGenMaxMemoryBeforeGC()});
        dataPublisher.publish(event);

        dataPublisher.shutdown();

    }

    public void publishLogEvents(long date, MemoryUsageLog memoryLog) throws DataEndpointException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointConfigurationException {

        DataPublisher dataPublisher = new DataPublisher(type, url, authURL, username, password);
        String streamId = DataBridgeCommonsUtils.generateStreamId(HTTPD_LOG_STREAM, VERSION);

        Event event = new Event(streamId, date, null, null,
                new Object[]{memoryLog.getMaxHeapMemory(),
                        memoryLog.getAllocatedHeapMemory(),
                        memoryLog.getUsedHeapMemory(),
                        memoryLog.getMaxNonHeapMemory(),
                        memoryLog.getAllocatedNonHeapMemory(),
                        memoryLog.getUsedNonHeapMemory(),
                        memoryLog.getPendingFinalizations()});

        dataPublisher.publish(event);

        dataPublisher.shutdown();

    }

    public void publishLogEvents(String fileName) throws DataEndpointException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointConfigurationException,
            FileNotFoundException {

        DataPublisher dataPublisher = new DataPublisher(type, url, authURL, username, password);
        String streamId = DataBridgeCommonsUtils.generateStreamId(HTTPD_LOG_STREAM, VERSION);

        Scanner scanner = new Scanner(new FileInputStream(fileName));
        while (scanner.hasNextLine()) {
            String stringLog = scanner.nextLine();

            ExtractGCData eObj = new ExtractGCData();
            ArrayList gcData = eObj.getGCData(stringLog);

            if (gcData == null) {
                continue;
            }

            Event event = new Event(streamId, System.currentTimeMillis(), null, null,
                    new Object[]{gcData.get(0), gcData.get(1), gcData.get(2), gcData.get(3), gcData.get(4),
                            gcData.get(5), gcData.get(6), gcData.get(7), gcData.get(8), gcData.get(9), gcData.get(10),
                            gcData.get(11), gcData.get(12), gcData.get(13), gcData.get(14), gcData.get(15), gcData.get(16),
                            gcData.get(17), gcData.get(18), gcData.get(19), gcData.get(20), gcData.get(21)});

            dataPublisher.publish(event);

        }

        scanner.close();


        dataPublisher.shutdown();

    }


    public static String getDataAgentConfigPath() {
        File filePath = new File("jvm-monitor-agent" + File.separator + "src" + File.separator + "main" + File.separator + "resources");
        if (!filePath.exists()) {
            filePath = new File("test" + File.separator + "resources");
        }
        if (!filePath.exists()) {
            filePath = new File("resources");
        }
        return filePath.getAbsolutePath() + File.separator + "data-agent-conf.xml";
    }

    public static InetAddress getLocalAddress() throws SocketException, UnknownHostException {
        Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
        while (ifaces.hasMoreElements()) {
            NetworkInterface iface = ifaces.nextElement();
            Enumeration<InetAddress> addresses = iface.getInetAddresses();

            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                    return addr;
                }
            }
        }
        return InetAddress.getLocalHost();
    }

    private static String getProperty(String name, String def) {
        String result = System.getProperty(name);
        if (result == null || result.length() == 0 || result == "") {
            result = def;
        }
        return result;
    }


}
