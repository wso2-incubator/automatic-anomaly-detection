package jvmmonitor;/*
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

public class Agent {

//    final static Logger logger = Logger.getLogger(Agent.class);
//
//    static String pid = "19949";
//
//    //private static MemoryMonitor memoryMonitor = new MemoryMonitor();
//
//    public static void main(String[] args) throws InterruptedException, MonitoringNotStartedException {
//
//
//        logger.info("Currently running");
//        for (VirtualMachineDescriptor vmd : VirtualMachine.list())
//            logger.info(vmd.id() + "\t" + vmd.displayName());
//
//        try {
//            UsageMonitorAgent usageMonitor = new UsageMonitorAgent(pid);
//            usageMonitor.startMonitoring();
//
//            LinkedList<GarbageCollectionLog> gm = (LinkedList<GarbageCollectionLog>) usageMonitor.getGarbageCollectionMonitor().getGCUsages();
//
//
//            while (true) {
//
//                    if (gm.size() > 0 ){
//                        Date date = new Date(gm.poll().getStartTime());
//                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
//                        String formattedDate = sdf.format(date);
//                        System.out.println("Start time : " + formattedDate);
//
//                    }
//                Date date1 = new Date(usageMonitor.getUsageLog().getTimeStamp());
//                SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
//                String formattedDate1 = sdf1.format(date1);
//                System.out.println("Usage Received Time : " + formattedDate1);
//
////                Map<String,Long> mem_usage = (Map<String,Long>) usageMonitor.getUsageLog().get(UsageMonitorAgent.MEMORY_USAGE_LOG);
////                logger.info(MemoryUsageMonitor.ALLOCATED_HEAP_MEMORY.concat(" : ").concat(String.valueOf(mem_usage.get(MemoryUsageMonitor.ALLOCATED_HEAP_MEMORY))));
////                logger.info(MemoryUsageMonitor.USED_HEAP_MEMORY.concat(" : ").concat(String.valueOf(mem_usage.get(MemoryUsageMonitor.USED_HEAP_MEMORY))));
////                logger.info(MemoryUsageMonitor.MAX_HEAP_MEMORY.concat(" : ").concat(String.valueOf(mem_usage.get(MemoryUsageMonitor.MAX_HEAP_MEMORY))));
////                logger.info(MemoryUsageMonitor.PENDING_FINALIZATIONS.concat(" : ").concat(String.valueOf(mem_usage.get(MemoryUsageMonitor.PENDING_FINALIZATIONS))));
////
//                Thread.sleep(1000);
////
////                logger.info("");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (AttachNotSupportedException e) {
//            e.printStackTrace();
//        } catch (MalformedObjectNameException e) {
//            e.printStackTrace();
////        } catch (MonitoringNotStartedException e) {
////            e.printStackTrace();
//        }
//
//    }

}