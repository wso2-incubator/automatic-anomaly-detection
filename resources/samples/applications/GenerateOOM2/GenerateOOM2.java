package samples.applications.GenerateOOM2;/*
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
public class GenerateOOM2 {

    /**
     * Parameters:-
     * <p>
     * Thread sleep time (ms)
     * Number of Thread
     * <p>
     * Eg:- (0 2)
     */
    public static void main(String[] args) {

        int sleepTime = 0;
        int numberOfThread = 1;

        if (args.length == 1) {

            try {
                sleepTime = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println(e);
            }

        } else if (args.length == 2) {

            try {
                sleepTime = Integer.parseInt(args[0]);
                numberOfThread = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println(e);
            }

        }

        for (int x = 0; x < numberOfThread; x++) {
            Thread t = new Thread(new GenerateOOME());
            GenerateOOME.threadSleepTime = sleepTime;
            t.start();
        }

    }


}

class GenerateOOME implements Runnable {

    public static int threadSleepTime = 0;
    private List<Integer> numbers = new ArrayList<Integer>();

    private void addNumber() {

        int i = 0;
        while (true) {
            int number = i;
            number *= 3;
            numbers.add(number);
            i++;

            try {
                Thread.sleep(threadSleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void run() {
        addNumber();
    }

}

