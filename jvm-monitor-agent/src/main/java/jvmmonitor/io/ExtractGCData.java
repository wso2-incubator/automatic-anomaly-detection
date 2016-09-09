package jvmmonitor.io;

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

import org.apache.log4j.Logger;

import java.util.ArrayList;

public class ExtractGCData {

    private String Date;
    private String TimeStarted;
    private double TimePass;
    private String GCFlage;
    private String CaseCollection;
    private String GCName;
    private long YoungGenerationBefore;
    private long YoungGenerationAfter;
    private long TotalYoungGeneration;
    private long OldGenerationBefore;
    private long OldGenerationAfter;
    private long TotalOldGeneration;
    private long MetaspaceGenerationBefore;
    private long MetaspaceGenerationAfter;
    private long TotalMetaspaceGeneration;
    private long TotalUsedHeapBefore;
    private long TotalUsedHeapAfter;
    private long TotalAvailableHeap;
    private double GCEventDuration;
    private double GCEventUserTimes;
    private double GCEventSysTimes;
    private double GCEventRealTimes;
    private ArrayList gcData = new ArrayList(22);

    final static Logger logger = Logger.getLogger(ExtractGCData.class);

    public ArrayList getGCData(String message) {

        //message = "2016-08-29T17:05:46.296+0530: 8.769: [GC (Allocation Failure) [PSYoungGen: 580951K->2541K(642560K)] 637171K->84387K(726016K), 0.0060124 secs] [Times: user=0.01 sys=0.01, real=0.00 secs]";
        //message = "16-08-29T17:05:46.302+0530: 8.775: [Full GC (Ergonomics) [PSYoungGen: 2541K->0K(642560K)] [ParOldGen: 81845K->21174K(69632K)] 84387K->21174K(712192K), [Metaspace: 3063K->3063K(1056768K)], 0.0068595 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]";
        //logger.info("GC log : " + message );

        try {

            int couCol = 0, index;
            Date = message.substring(0, index = message.indexOf('T'));
            message = message.substring(index + 1);

            for (int i = 0; i < message.length(); i++) {
                if (message.charAt(i) == ':') {
                    if (couCol == 2) {
                        TimeStarted = message.substring(0, i);
                        index = i;
                    }
                    if (couCol == 3) {
                        TimePass = Double.parseDouble(message.substring(index + 1, i).replaceAll("\\s+", ""));
                        message = message.substring(i + 1);
                        break;
                    }
                    couCol++;
                }
            }

            index = message.indexOf('(');
            GCFlage = message.substring(message.indexOf('[') + 1, index).replaceAll("\\s+", "");
            CaseCollection = message.substring(index + 1, index = message.indexOf(')'));
            message = message.substring(index + 1);

            /**
             * Extract data according Minor GC
             */
            if (GCFlage.equals("GC")) {

                GCName = message.substring(message.indexOf('[') + 1, index = message.indexOf(':'));
                YoungGenerationBefore = Integer.parseInt(message.substring(index + 1, index = message.indexOf('K')).replaceAll("\\s+", ""));
                message = message.substring(index + 1);
                YoungGenerationAfter = Integer.parseInt(message.substring(message.indexOf('>') + 1, index = message.indexOf('K')).replaceAll("\\s+", ""));
                message = message.substring(index + 1);
                TotalYoungGeneration = Integer.parseInt(message.substring(message.indexOf('(') + 1, index = message.indexOf('K')).replaceAll("\\s+", ""));
                message = message.substring(index + 1);

                TotalUsedHeapBefore = Integer.parseInt(message.substring(message.indexOf(']') + 1, index = message.indexOf('K')).replaceAll("\\s+", ""));
                message = message.substring(index + 1);
                TotalUsedHeapAfter = Integer.parseInt(message.substring(message.indexOf('>') + 1, index = message.indexOf('K')).replaceAll("\\s+", ""));
                message = message.substring(index + 1);
                TotalAvailableHeap = Integer.parseInt(message.substring(message.indexOf('(') + 1, index = message.indexOf('K')).replaceAll("\\s+", ""));
                message = message.substring(index + 1);

                GCEventDuration = Double.parseDouble(message.substring(message.indexOf(',') + 1, index = message.indexOf("secs")).replaceAll("\\s+", ""));
                message = message.substring(index + 4);
                GCEventUserTimes = Double.parseDouble(message.substring(message.indexOf("r=") + 2, message.indexOf("sys")).replaceAll("\\s+", ""));
                GCEventSysTimes = Double.parseDouble(message.substring(message.indexOf("s=") + 2, message.indexOf(',')).replaceAll("\\s+", ""));
                GCEventRealTimes = Double.parseDouble(message.substring(message.indexOf("l=") + 2, message.indexOf("secs")).replaceAll("\\s+", ""));

            }

            /**
             * Extract data according Major GC
             */
            if (GCFlage.equals("FullGC")) {

                GCName = message.substring(message.indexOf('[') + 1, index = message.indexOf(':'));
                YoungGenerationBefore = Integer.parseInt(message.substring(index + 1, index = message.indexOf('K')).replaceAll("\\s+", ""));
                message = message.substring(index + 1);
                YoungGenerationAfter = Integer.parseInt(message.substring(message.indexOf('>') + 1, index = message.indexOf('K')).replaceAll("\\s+", ""));
                message = message.substring(index + 1);
                TotalYoungGeneration = Integer.parseInt(message.substring(message.indexOf('(') + 1, index = message.indexOf('K')).replaceAll("\\s+", ""));
                message = message.substring(index + 1);

                OldGenerationBefore = Integer.parseInt(message.substring(message.indexOf(':') + 1, index = message.indexOf('K')).replaceAll("\\s+", ""));
                message = message.substring(index + 1);
                OldGenerationAfter = Integer.parseInt(message.substring(message.indexOf('>') + 1, index = message.indexOf('K')).replaceAll("\\s+", ""));
                message = message.substring(index + 1);
                TotalOldGeneration = Integer.parseInt(message.substring(message.indexOf('(') + 1, index = message.indexOf('K')).replaceAll("\\s+", ""));
                message = message.substring(index + 1);

                TotalUsedHeapBefore = Integer.parseInt(message.substring(message.indexOf(']') + 1, index = message.indexOf('K')).replaceAll("\\s+", ""));
                message = message.substring(index + 1);
                TotalUsedHeapAfter = Integer.parseInt(message.substring(message.indexOf('>') + 1, index = message.indexOf('K')).replaceAll("\\s+", ""));
                message = message.substring(index + 1);
                TotalAvailableHeap = Integer.parseInt(message.substring(message.indexOf('(') + 1, index = message.indexOf('K')).replaceAll("\\s+", ""));
                message = message.substring(index + 1);

                MetaspaceGenerationBefore = Integer.parseInt(message.substring(message.indexOf(':') + 1, index = message.indexOf('K')).replaceAll("\\s+", ""));
                message = message.substring(index + 1);
                MetaspaceGenerationAfter = Integer.parseInt(message.substring(message.indexOf('>') + 1, index = message.indexOf('K')).replaceAll("\\s+", ""));
                message = message.substring(index + 1);
                TotalMetaspaceGeneration = Integer.parseInt(message.substring(message.indexOf('(') + 1, index = message.indexOf('K')).replaceAll("\\s+", ""));
                message = message.substring(index + 1);

                GCEventDuration = Double.parseDouble(message.substring(message.indexOf(',') + 1, index = message.indexOf("secs")).replaceAll("\\s+", ""));
                message = message.substring(index + 4);
                GCEventUserTimes = Double.parseDouble(message.substring(message.indexOf("r=") + 2, message.indexOf("sys")).replaceAll("\\s+", ""));
                GCEventSysTimes = Double.parseDouble(message.substring(message.indexOf("s=") + 2, message.indexOf(',')).replaceAll("\\s+", ""));
                GCEventRealTimes = Double.parseDouble(message.substring(message.indexOf("l=") + 2, message.indexOf("secs")).replaceAll("\\s+", ""));

            }

            gcData.add(Date);
            gcData.add(TimeStarted);
            gcData.add(TimePass);
            gcData.add(GCFlage);
            gcData.add(CaseCollection);
            gcData.add(GCName);
            gcData.add(YoungGenerationBefore);
            gcData.add(YoungGenerationAfter);
            gcData.add(TotalYoungGeneration);
            gcData.add(OldGenerationBefore);
            gcData.add(OldGenerationAfter);
            gcData.add(TotalOldGeneration);
            gcData.add(MetaspaceGenerationBefore);
            gcData.add(MetaspaceGenerationAfter);
            gcData.add(TotalMetaspaceGeneration);
            gcData.add(TotalUsedHeapBefore);
            gcData.add(TotalUsedHeapAfter);
            gcData.add(TotalAvailableHeap);
            gcData.add(GCEventDuration);
            gcData.add(GCEventUserTimes);
            gcData.add(GCEventSysTimes);
            gcData.add(GCEventRealTimes);

        } catch (IndexOutOfBoundsException e) {
            logger.error(e);
            return null;
        } catch (NumberFormatException e) {
            logger.error(e);
            return null;
        } catch (Exception e) {
            logger.error(e);
            return null;
        }


        /*
        System.out.println(Date);
        System.out.println(TimeStarted);
        System.out.println(TimePass);
        System.out.println(Flage);
        System.out.println(CaseCollection);
        System.out.println(GCName);
        System.out.println(YoungGenerationBefore);
        System.out.println(YoungGenerationAfter);
        System.out.println(TotalYoungGeneration);
        System.out.println(OldGenerationBefore);
        System.out.println(OldGenerationAfter);
        System.out.println(TotalOldGeneration);
        System.out.println(MetaspaceGenerationBefore);
        System.out.println(MetaspaceGenerationAfter);
        System.out.println(TotalMetaspaceGeneration);
        System.out.println(TotalUsedHeapBefore);
        System.out.println(TotalUsedHeapAfter);
        System.out.println(TotalAvailableHeap);
        System.out.println(GCEventDuration);
        System.out.println(GCEventUserTimes);
        System.out.println(GCEventSysTimes);
        System.out.println(GCEventRealTimes);
        */

        return gcData;

    }


}
