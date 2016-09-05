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

import com.sun.tools.attach.AttachNotSupportedException;
import jvmmonitor.UsageMonitor;
import jvmmonitor.exceptions.MonitoringNotStartedException;
import jvmmonitor.management.GarbageCollectionMonitor;
import jvmmonitor.management.MemoryUsageMonitor;
import org.wso2.carbon.databridge.agent.AgentHolder;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import org.wso2.carbon.databridge.commons.utils.DataBridgeCommonsUtils;

import javax.management.MalformedObjectNameException;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;

public class HttpdAgent {

    private static String HTTPD_LOG_STREAM;
    private static String VERSION;
    private static int defaultThriftPort;
    private static int defaultBinaryPort;

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

        System.out.println("Starting DAS HttpLog Agent");
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

    public void publishLogEvents() throws DataEndpointException,
            DataEndpointAuthenticationException,
            DataEndpointAgentConfigurationException,
            TransportException,
            DataEndpointConfigurationException {

        DataPublisher dataPublisher = new DataPublisher(type, url, authURL, username, password);

        String streamId = DataBridgeCommonsUtils.generateStreamId(HTTPD_LOG_STREAM, VERSION);
        publishLogEvents(dataPublisher, streamId);
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

    private static void publishLogEvents(DataPublisher dataPublisher, String streamId) {

        UsageMonitor logObj = null;

        try {
            logObj = new UsageMonitor("");
            logObj.stratMonitoring();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AttachNotSupportedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }

        int i = 1;
        while (true) {
            System.out.println("Publish Memory data : " + i++);

            try {

                Map<String, Object> usagesData = logObj.getUsageLog();
                ArrayList<Map<String, String>> gcLog = (ArrayList<Map<String, String>>) usagesData.get(UsageMonitor.GARBAGE_COLLECTION_LOG);
                Map<String, Long> memoryUL = (Map<String, Long>) usagesData.get(UsageMonitor.MEMORY_USAGE_LOG);

                if (gcLog.isEmpty()) {

                    Event event = new Event(streamId, System.currentTimeMillis(), null, null,
                            new Object[]{memoryUL.get(MemoryUsageMonitor.MAX_HEAP_MEMORY)
                                    , memoryUL.get(MemoryUsageMonitor.ALLOCATED_HEAP_MEMORY)
                                    , memoryUL.get(MemoryUsageMonitor.USED_HEAP_MEMORY)
                                    , memoryUL.get(MemoryUsageMonitor.MAX_NON_HEAP_MEMORY)
                                    , memoryUL.get(MemoryUsageMonitor.ALLOCATED_NON_HEAP_MEMORY)
                                    , memoryUL.get(MemoryUsageMonitor.USED_NON_HEAP_MEMORY)
                                    , memoryUL.get(MemoryUsageMonitor.PENDING_FINALIZATIONS)
                            });
                    dataPublisher.publish(event);

                } else {
                    for (Map<String, String> gcmap : gcLog) {

                        Event event = new Event(streamId, System.currentTimeMillis(), null, null,
                                new Object[]{gcmap.get(GarbageCollectionMonitor.GC_TYPE)
                                        , gcmap.get(GarbageCollectionMonitor.GC_DURATION)
                                        , gcmap.get(GarbageCollectionMonitor.GC_START_TIME)
                                        , gcmap.get(GarbageCollectionMonitor.GC_CAUSE)
                                        , gcmap.get(GarbageCollectionMonitor.EDEN_SPACE_USED_MEMORY_AFTER_GC)
                                        , gcmap.get(GarbageCollectionMonitor.EDEN_SPACE_USED_MEMORY_BEFORE_GC)
                                        , gcmap.get(GarbageCollectionMonitor.SURVIVOR_SPACE_USED_MEMORY_AFTER_GC)
                                        , gcmap.get(GarbageCollectionMonitor.SURVIVOR_SPACE_USED_MEMORY_BEFORE_GC)
                                        , gcmap.get(GarbageCollectionMonitor.OLD_GEN_USED_MEMORY_AFTER_GC)
                                        , gcmap.get(GarbageCollectionMonitor.OLD_GEN_USED_MEMORY_BEFORE_GC)
                                        , gcmap.get(GarbageCollectionMonitor.EDEN_SPACE_COMMITTED_MEMORY_AFTER_GC)
                                        , gcmap.get(GarbageCollectionMonitor.EDEN_SPACE_COMMITTED_MEMORY_BEFORE_GC)
                                        , gcmap.get(GarbageCollectionMonitor.SURVIVOR_SPACE_COMMITTED_MEMORY_AFTER_GC)
                                        , gcmap.get(GarbageCollectionMonitor.SURVIVOR_SPACE_COMMITTED_MEMORY_BEFORE_GC)
                                        , gcmap.get(GarbageCollectionMonitor.OLD_GEN_COMMITTED_MEMORY_AFTER_GC)
                                        , gcmap.get(GarbageCollectionMonitor.OLD_GEN_COMMITTED_MEMORY_BEFORE_GC)
                                        , gcmap.get(GarbageCollectionMonitor.EDEN_SPACE_MAX_MEMORY_AFTER_GC)
                                        , gcmap.get(GarbageCollectionMonitor.EDEN_SPACE_MAX_MEMORY_BEFORE_GC)
                                        , gcmap.get(GarbageCollectionMonitor.SURVIVOR_SPACE_MAX_MEMORY_AFTER_GC)
                                        , gcmap.get(GarbageCollectionMonitor.SURVIVOR_SPACE_MAX_MEMORY_BEFORE_GC)
                                        , gcmap.get(GarbageCollectionMonitor.OLD_GEN_MAX_MEMORY_AFTER_GC)
                                        , gcmap.get(GarbageCollectionMonitor.OLD_GEN_MAX_MEMORY_BEFORE_GC)
                                        , memoryUL.get(MemoryUsageMonitor.MAX_HEAP_MEMORY)
                                        , memoryUL.get(MemoryUsageMonitor.ALLOCATED_HEAP_MEMORY)
                                        , memoryUL.get(MemoryUsageMonitor.USED_HEAP_MEMORY)
                                        , memoryUL.get(MemoryUsageMonitor.MAX_NON_HEAP_MEMORY)
                                        , memoryUL.get(MemoryUsageMonitor.ALLOCATED_NON_HEAP_MEMORY)
                                        , memoryUL.get(MemoryUsageMonitor.USED_NON_HEAP_MEMORY)
                                        , memoryUL.get(MemoryUsageMonitor.PENDING_FINALIZATIONS)
                                });
                        dataPublisher.publish(event);

                    }
                }


            } catch (MonitoringNotStartedException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


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
