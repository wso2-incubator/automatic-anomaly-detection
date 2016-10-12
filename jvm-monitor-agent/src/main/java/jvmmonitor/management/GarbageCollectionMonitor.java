package jvmmonitor.management;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;
import jvmmonitor.model.GarbageCollectionLog;
import jvmmonitor.util.GarbageCollectionListener;
import org.apache.log4j.Logger;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * Collect the Garbage Collection logs from any connected JVMs
 *
 */
public class GarbageCollectionMonitor {

    private List<GarbageCollectorMXBean> gcBeans;
    private List<GarbageCollectionLog> gcUsages;
    private List<GarbageCollectionListener> listeners;

    private long jvmStartTime;

    //  --<! DO NOT CHANGE!>-- Memory management types
    private final static String EDEN_SPACE = "PS Eden Space";
    private final static String CODE_CACHE= "Code Cache";
    private final static String COMPRESSED_CLASS_SPACE = "Compressed Class Space";
    private final static String SURVIVOR_SPACE = "PS Survivor Space";
    private final static String OLD_GENERATION_SPACE = "PS Old Gen";
    private final static String METASPACE = "Metaspace";

    final static Logger logger = Logger.getLogger(GarbageCollectionMonitor.class);


    /**
     * Constructor
     *
     * Obtain the Garbage Collection MX beans from any given Server Connection to the JVMs
     * Construct the obj using those GC beans
     *
     * Added notifications to trigger when any garbage collection activity happens
     *
     * @param serverConnection
     * @throws InterruptedException
     * @throws MalformedObjectNameException
     */
    public GarbageCollectionMonitor(MBeanServerConnection serverConnection) throws MalformedObjectNameException,
            IOException {

        this.gcUsages = new LinkedList<GarbageCollectionLog>();
        this.listeners = new ArrayList<GarbageCollectionListener>();

        Set<ObjectName> gcnames = serverConnection.queryNames(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=*"), null);
        this.gcBeans = new ArrayList<GarbageCollectorMXBean>(gcnames.size());

        GarbageCollectorMXBean gcbean;

        //get all the GarbageCollectorMXBeans - there's one for each heap generation
        //so probably two - the old generation and young generation
        for (ObjectName on : gcnames) {
            gcbean = ManagementFactory.newPlatformMXBeanProxy(serverConnection, on.toString(), GarbageCollectorMXBean.class);
            gcBeans.add(gcbean);

            NotificationEmitter emitter = (NotificationEmitter) gcbean;
            NotificationListener listener = new GCNotificationListener();
            emitter.addNotificationListener(listener, null, null);
        }


        this.jvmStartTime = ManagementFactory.newPlatformMXBeanProxy(serverConnection, ManagementFactory.RUNTIME_MXBEAN_NAME , RuntimeMXBean.class).getStartTime();
        logger.info("Start time jvm " + jvmStartTime );
    }


    /**
     * Implements a notification listener to listen notifications happens after Garbage collection
     *
     */
    protected  class GCNotificationListener implements NotificationListener {

        /**
         * implement the notifier callback handler
         *  when gc event happens this method will be executed
         *  collect gc log data and add it to gc log queue
         *  trigger the GarbageCollectionListeners
         * @param notification
         * @param handback
         */
        public void handleNotification(Notification notification, Object handback) {

            //only handle GARBAGE_COLLECTION_NOTIFICATION notifications here
            if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {

                //get the information associated with this notification
                GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());

                //get all the info
                String gctype = info.getGcAction();

                if ("end of minor GC".equals(gctype)) {
                    gctype = "minor";
                } else if ("end of major GC".equals(gctype)) {
                    gctype = "major";
                }

                GarbageCollectionLog gclog = new GarbageCollectionLog();
                GcInfo gcInfo;
                Map<String, MemoryUsage> memoryUsageMap;
                MemoryUsage memoryUsage;

                gcInfo = info.getGcInfo();

                logger.info("GC collected >> GC type :".concat(gctype).concat(" GC Start Time :")
                        .concat(String.valueOf(gcInfo.getStartTime())) );

                //=========memory Usage After GC==============================
                memoryUsageMap= gcInfo.getMemoryUsageAfterGc();

                //eden space memory management
                memoryUsage = memoryUsageMap.get(EDEN_SPACE);
                gclog.setEdenCommittedMemoryAfterGC(memoryUsage.getCommitted());
                gclog.setEdenMaxMemoryAfterGC(memoryUsage.getMax());
                gclog.setEdenUsedMemoryAfterGC(memoryUsage.getUsed());

                //survivor space memory management
                memoryUsage = memoryUsageMap.get(SURVIVOR_SPACE);
                gclog.setSurvivorCommittedMemoryAfterGC(memoryUsage.getCommitted());
                gclog.setSurvivorMaxMemoryAfterGC(memoryUsage.getMax());
                gclog.setSurvivorUsedMemoryAfterGC(memoryUsage.getUsed());

                //old gen space memory management
                memoryUsage = memoryUsageMap.get(OLD_GENERATION_SPACE);
                gclog.setOldGenCommittedMemoryAfterGC(memoryUsage.getCommitted());
                gclog.setOldGenMaxMemoryAfterGC(memoryUsage.getMax());
                gclog.setOldGenUsedMemoryAfterGC(memoryUsage.getUsed());


                //============================================================

                //===========memory management before GC============================
                memoryUsageMap= gcInfo.getMemoryUsageBeforeGc();

                //eden space memory management
                memoryUsage = memoryUsageMap.get(EDEN_SPACE);
                gclog.setEdenCommittedMemoryBeforeGC(memoryUsage.getCommitted());
                gclog.setEdenMaxMemoryBeforeGC(memoryUsage.getMax());
                gclog.setEdenUsedMemoryBeforeGC(memoryUsage.getUsed());

                //survivor space memory management
                memoryUsage = memoryUsageMap.get(SURVIVOR_SPACE);
                gclog.setSurvivorCommittedMemoryBeforeGC(memoryUsage.getCommitted());
                gclog.setSurvivorMaxMemoryBeforeGC(memoryUsage.getMax());
                gclog.setSurvivorUsedMemoryBeforeGC(memoryUsage.getUsed());

                //old gen space memory management
                memoryUsage = memoryUsageMap.get(OLD_GENERATION_SPACE);
                gclog.setOldGenCommittedMemoryBeforeGC(memoryUsage.getCommitted());
                gclog.setOldGenMaxMemoryBeforeGC(memoryUsage.getMax());
                gclog.setOldGenUsedMemoryBeforeGC(memoryUsage.getUsed());

                //============================================================


                //=====================general info===========================
                gclog.setDuration(gcInfo.getDuration());
                gclog.setGcCause(info.getGcCause());
                gclog.setStartTime(gcInfo.getEndTime() + jvmStartTime );
                gclog.setGcType(gctype);
                //============================================================

                synchronized (gcUsages){
                    gcUsages.add(gclog);
                }

                //notify the GarbageCollectionListeners that GC logs has arrived
                if (gcUsages.size() > 0){
                    for ( GarbageCollectionListener l : listeners) {
                        l.processGClogs((LinkedList<GarbageCollectionLog>) gcUsages);
                    }
                }

            }
        }
    }


    /**
     * Register GarbageCollectionListeners
     * Notification listeners can listen to notifications trigger when any GC log arrives
     * @param listener
     */
    public void registerListener(GarbageCollectionListener listener){
        this.listeners.add(listener);
    }

    /**
     * Return garbage collection usages reference
     */
    public List<GarbageCollectionLog> getGCUsages() {
        return gcUsages;
    }


    //====================setters and getters=============================
    public List<GarbageCollectorMXBean> getGcBeans() {
        return gcBeans;
    }

    public void setGcBeans(List<GarbageCollectorMXBean> gcBeans) {
        this.gcBeans = gcBeans;
    }
}