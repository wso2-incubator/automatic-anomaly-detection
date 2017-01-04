package samples.applications.NormalApp;/*
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
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class provides normal application scenarios
 */
public class NormalApp {

    /**
     * Parameters:-
     * <p>
     * int  Numbers of Threads
     * int  Thread Pool size
     * int  Max main list size
     * int  Array size
     * long Threads sleep time
     * <p>
     * You can add parameters in two type
     * 1). Numbers of Threads, Thread Pool size (eg:- 3 5)
     * 2). Numbers of Threads, Thread Pool size, Max main list size, Array size, Threads sleep time (eg:- 3 5 100000 10 100)
     */
    public static void main(String[] args) {

        int numbersOfThreads = 5;
        int threadPoolSize = 12;
        int maxMainListSize = 10000000;
        int arraySize = 10;
        long threadsSleepTime = 100;
        Random randomGenerator = new Random();

        if (args.length == 2) {

            try {
                numbersOfThreads = Integer.parseInt(args[0]);
                threadPoolSize = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println(e);
            }

        } else if (args.length == 5) {

            try {
                numbersOfThreads = Integer.parseInt(args[0]);
                threadPoolSize = Integer.parseInt(args[1]);
                maxMainListSize = Integer.parseInt(args[2]);
                arraySize = Integer.parseInt(args[3]);
                threadsSleepTime = Integer.parseInt(args[4]);
            } catch (NumberFormatException e) {
                System.err.println(e);
            }

        }

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        ListSort.maxMainlistSize = maxMainListSize;
        for (int i = 0; i < numbersOfThreads; i++) {
            Runnable worker = new NumberBuilder(arraySize + randomGenerator.nextInt(500), threadsSleepTime);
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

    }

}

class NumberBuilder implements Runnable {

    private int size;
    private long sleepTime;
    private Random randomGenerator;
    private List<Integer> numbList;
    private int maxInt = 1000000;

    public NumberBuilder(int size, long sleepTime) {
        this.size = size;
        this.sleepTime = sleepTime;
        randomGenerator = new Random();
        numbList = new ArrayList<Integer>();
    }

    public void run() {
        ListSort listSort = new ListSort();
        while (true) {

            for (int x = 0; x < size; x++) {
                numbList.add(randomGenerator.nextInt(maxInt));
                listSort.bubbleSort(numbList);
            }
            listSort.addtoMainList(numbList);
            numbList.clear();

            try {
                Thread.sleep(sleepTime + randomGenerator.nextInt(500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}

class ListSort {

    public static int maxMainlistSize;
    public ArrayList<Integer> mainNumberList = new ArrayList<Integer>(maxMainlistSize * 2 + (new Random()).nextInt(1000));

    public synchronized void addtoMainList(List<Integer> numbers) {

        if (mainNumberList.size() > maxMainlistSize) {
            mainNumberList.clear();
        }

        mainNumberList.addAll(numbers);
        bubbleSort(mainNumberList);

    }

    public void bubbleSort(List<Integer> numList) {
        int i;
        boolean flag = true;
        int temp;

        while (flag) {
            flag = false;

            try {
                for (i = 0; i < numList.size() - 1; i++) {
                    if (numList.get(i) < numList.get(i + 1)) {
                        temp = numList.get(i);
                        numList.set(i, numList.get(i + 1));
                        numList.set(i + 1, temp);
                        flag = true;
                    }
                }
            } catch (Exception e) {
                break;
            }

        }
    }


}
