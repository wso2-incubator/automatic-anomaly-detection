package monitor.beans;

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

import com.sun.management.GarbageCollectionNotificationInfo;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Manage the Garbage Collection logs from any connected JVMs
 *
 */
public class GarbageCollectorBean {

    List<GarbageCollectorMXBean> gcBeans;
    List<Map<String, String>> gcUsages;

    /**
     * Constructor
     *
     * Obtain the Garbage Collection MX beans from any given Server Connection to the JVM
     * Construct the obj using those GC beans
     *
     * @param serverConnection
     * @return {List<GarbageCollectorMXBean>} list of GC MX beans
     * @throws InterruptedException
     * @throws MalformedObjectNameException
     */
    public GarbageCollectorBean(MBeanServerConnection serverConnection) throws InterruptedException, MalformedObjectNameException, IOException {

        Set<ObjectName> gcnames = serverConnection.queryNames(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=*"), null);
        this.gcBeans = new ArrayList<>(gcnames.size());

        GarbageCollectorMXBean gcbean;
        //get all the GarbageCollectorMXBeans - there's one for each heap generation
        //so probably two - the old generation and young generation
        for (ObjectName on : gcnames) {
            gcbean = ManagementFactory.newPlatformMXBeanProxy(serverConnection, on.toString(), GarbageCollectorMXBean.class);
            gcBeans.add(gcbean);

            NotificationEmitter emitter = (NotificationEmitter) gcbean;

            NotificationListener listener = new NotificationListener() {

                long totalGcDuration = 0;   //keep a count of the total time spent in GCs

                /**
                 *  implement the notifier callback handler
                 */
                @Override
                public void handleNotification(Notification notification, Object handback) {

                    System.out.println("handleNotification started");

                    //only handle GARBAGE_COLLECTION_NOTIFICATION notifications here
                    if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {

                        //get the information associated with this notification
                        GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
                        //get all the info and pretty print it
                        long duration = info.getGcInfo().getDuration();
                        String gctype = info.getGcAction();

                        if ("end of minor monitor.GC".equals(gctype)) {
                            gctype = "Young Gen monitor.GC";
                        } else if ("end of major monitor.GC".equals(gctype)) {
                            gctype = "Old Gen monitor.GC";
                        }

                        System.out.println();
                        System.out.println(gctype + ": - " + info.getGcInfo().getId()+ " " + info.getGcName() + " (from " + info.getGcCause()+") "+duration + " microseconds; start-end times " + info.getGcInfo().getStartTime()+ "-" + info.getGcInfo().getEndTime());
                        System.out.println("GcInfo CompositeType: " + info.getGcInfo().getCompositeType());
                        System.out.println("GcInfo MemoryUsageAfterGc: " + info.getGcInfo().getMemoryUsageAfterGc());
                        System.out.println("GcInfo MemoryUsageBeforeGc: " + info.getGcInfo().getMemoryUsageBeforeGc());

                        //Get the information about each memory space, and pretty print it
                        Map<String, MemoryUsage> membefore = info.getGcInfo().getMemoryUsageBeforeGc();
                        Map<String, MemoryUsage> mem = info.getGcInfo().getMemoryUsageAfterGc();
                        for (Map.Entry<String, MemoryUsage> entry : mem.entrySet()) {
                            String name = entry.getKey();
                            MemoryUsage memdetail = entry.getValue();
                            long memInit = memdetail.getInit();
                            long memCommitted = memdetail.getCommitted();
                            long memMax = memdetail.getMax();
                            long memUsed = memdetail.getUsed();
                            MemoryUsage before = membefore.get(name);
                            long beforepercent = ((before.getUsed()*1000L)/before.getCommitted());
                            long percent = ((memUsed*1000L)/before.getCommitted()); //>100% when it gets expanded

                            System.out.print(name + (memCommitted==memMax?"(fully expanded)":"(still expandable)") +"used: "+(beforepercent/10)+"."+(beforepercent%10)+"%->"+(percent/10)+"."+(percent%10)+"%("+((memUsed/1048576)+1)+"MB) / ");
                        }
                        System.out.println();
                        totalGcDuration += info.getGcInfo().getDuration();
                        long percent = totalGcDuration*1000L/info.getGcInfo().getEndTime();
                        System.out.println("monitor.GC cumulated overhead "+(percent/10)+"."+(percent%10)+"%");
                    }
                }

            };

            emitter.addNotificationListener(listener, null, null);
        }



    }

}
