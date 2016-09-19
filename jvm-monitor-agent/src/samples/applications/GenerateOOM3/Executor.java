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

import java.util.ArrayList;
import java.util.List;

/**
 * This is a sample Java app for testing "OOM scenario".
 */
public class Executor {

    /**
     * Parameters:-
     * <p>
     * Number Of Iteration
     * Memory Growth Time (ms)
     * Memory Free Time   (ms)
     * Sleep Time         (ms)
     * <p>
     * Eg:- (5 5000 5000 0)
     */
    public static void main(String[] args) {

        int numberOfIteration = 5;
        int memoryGrowthTime = 5000;
        int memoryFreeTime = 5000;
        int sleepTime = 0;

        if (args.length == 3) {
            try {
                numberOfIteration = Integer.parseInt(args[0]);
                memoryGrowthTime = Integer.parseInt(args[1]);
                memoryFreeTime = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.err.println(e);
            }
        } else if (args.length == 4) {
            try {
                numberOfIteration = Integer.parseInt(args[0]);
                memoryGrowthTime = Integer.parseInt(args[1]);
                memoryFreeTime = Integer.parseInt(args[2]);
                sleepTime = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                System.err.println(e);
            }
        }

        //Start delay
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        for (int x = 0; x < numberOfIteration; x++) {

            TestOOM lp = new TestOOM();
            lp.setThreadSleepTime(sleepTime);
            lp.setMemoryGrowthTime(memoryGrowthTime);
            lp.addNumber();

            try {
                Thread.sleep(memoryFreeTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        while (true) ;

    }

}

class TestOOM {

    private int threadSleepTime = 0;
    private int memoryGrowthTime = 5000;
    private long time = System.currentTimeMillis();

    public void setThreadSleepTime(int threadSleepTime) {
        this.threadSleepTime = threadSleepTime;
    }

    public void setMemoryGrowthTime(int memoryGrowthTime) {
        this.memoryGrowthTime = memoryGrowthTime;
    }

    public void addNumber() {

        List<Integer> numbers = new ArrayList<Integer>();

        int i = 0;
        while (true) {
            numbers.add(i);
            i++;

            try {
                Thread.sleep(threadSleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if ((System.currentTimeMillis() - time) > memoryGrowthTime) {
                numbers.clear();
                System.gc();
                System.runFinalization();
                break;

            }
        }

    }

}
