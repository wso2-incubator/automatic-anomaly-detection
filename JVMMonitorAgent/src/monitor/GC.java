package monitor;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.tools.attach.*;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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


public class GC {

    static final String CONNECTOR_ADDRESS =
            "com.sun.management.jmxremote.localConnectorAddress";


    public static void main(String[] args) throws InterruptedException , MalformedObjectNameException{
        installGCMonitoring();
//        printStats("5164");
    }



    public static List<GarbageCollectorMXBean> printStats(String id) throws InterruptedException, MalformedObjectNameException {
        try
        {

            VirtualMachine vm=VirtualMachine.attach(id);
            System.out.println("Connected to "+vm.id());
            System.out.println("System Properties:");
            for(Map.Entry<?,?> en:vm.getSystemProperties().entrySet())
                System.out.println("\t"+en.getKey()+" = "+en.getValue());
            System.out.println();
            try
            {
                MBeanServerConnection mbs = connect(vm);
                Set<ObjectName> gcnames = mbs.queryNames(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=*"), null);
                List<GarbageCollectorMXBean> gcBeans = new ArrayList<>(gcnames.size());
                for(ObjectName on: gcnames) {
                    gcBeans.add(ManagementFactory.newPlatformMXBeanProxy(mbs, on.toString(), GarbageCollectorMXBean.class));
                }

//                MBeanServerConnection sc=connect(vm);
//                GarbageCollectorMXBean[] gcs = ManagementFactory.newPlatformMXBeanProxy(sc, ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE+ " ,name=*", GarbageCollectorMXBean.class);
//                System.out.println();

//                while (true) {
//                    Thread.sleep(100);
//                    System.out.println("Scavenger used: " + gcBeans.toArray()[0]);
//                }

                return gcBeans;
            } catch(AgentLoadException | AgentInitializationException ex)
            {
                System.out.println("JMX: "+ex);
            }
            vm.detach();

        } catch(AttachNotSupportedException | IOException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }


    static MBeanServerConnection connect(VirtualMachine vm)
            throws AgentLoadException, AgentInitializationException, IOException
    {
        String connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
        if(connectorAddress == null)
        {
            System.out.println("loading agent");
            Properties props = vm.getSystemProperties();
            String home  = props.getProperty("java.home");
            String agent = home+ File.separator+"lib"+File.separator+"management-agent.jar";
            vm.loadAgent(agent);
            connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
            while(connectorAddress==null) try {
                Thread.sleep(1000);
                connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
            } catch(InterruptedException ex){}
        }
        JMXConnector c= JMXConnectorFactory.connect(new JMXServiceURL(connectorAddress));
        return c.getMBeanServerConnection();
    }


    public static void installGCMonitoring() throws MalformedObjectNameException , InterruptedException{

        //get all the GarbageCollectorMXBeans - there's one for each heap generation
        //so probably two - the old generation and young generation
        List<GarbageCollectorMXBean> gcbeans = printStats("5164");
        //Install a notifcation handler for each bean
        for (GarbageCollectorMXBean gcbean : gcbeans) {
            System.out.println(gcbean);
            NotificationEmitter emitter = (NotificationEmitter) gcbean;
            //use an anonymously generated listener for this example
            // - proper code should really use a named class
            NotificationListener listener = new NotificationListener() {
                //keep a count of the total time spent in GCs
                long totalGcDuration = 0;
                //implement the notifier callback handler
                @Override
                public void handleNotification(Notification notification, Object handback) {

                    System.out.println("handleNotification started");
                    //we only handle GARBAGE_COLLECTION_NOTIFICATION notifications here
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
                        //System.out.println("GcInfo CompositeType: " + info.getGcInfo().getCompositeType());
                        //System.out.println("GcInfo MemoryUsageAfterGc: " + info.getGcInfo().getMemoryUsageAfterGc());
                        //System.out.println("GcInfo MemoryUsageBeforeGc: " + info.getGcInfo().getMemoryUsageBeforeGc());

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

            //Add the listener

            emitter.addNotificationListener(listener, null, null);


            while (true){


                int iteratorValue = 10;

                for (int outerIterator = 1; outerIterator < 20000; outerIterator++) {
//            System.out.println(" 1 Iteration " + outerIterator + " Free Mem: " + Runtime.getRuntime().freeMemory());

                    int loop1 = 2;
                    int[] memoryFillIntVar = new int[iteratorValue];

                    // feel memoryFillIntVar array in loop..
                    do {
                        memoryFillIntVar[loop1] = 0;
                        loop1--;
                    } while (loop1 > 0);

                    //increase the length of next array
                    iteratorValue = (int)(iteratorValue * 1) + 10000;
//            System.out.println("\nRequired Memory for next loop: " + iteratorValue);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }



                }
            }
        }
    }
}
