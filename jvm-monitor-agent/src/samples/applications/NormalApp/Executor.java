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
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class provides normal application scenarios
 */
public class Executor {

    /**
     * Parameters:-
     * <p>
     * int  Numbers of Threads
     * int  Thread Pool size
     * int  Max main list size
     * int  Array size
     * long Threads sleep time
     *
     * eg:- 3 5 100000 10 1500
     */
    public static void main(String[] args) {

        int numbersOfThread = Integer.parseInt(args[0]);
        ExecutorService executor = Executors.newFixedThreadPool(Integer.parseInt(args[1]));
        ListSort.maxMainlistSize = Integer.parseInt(args[2]);
        for (int i = 0; i < numbersOfThread; i++) {
            Runnable worker = new NumberBuilder(Integer.parseInt(args[3]), Integer.parseInt(args[4]));
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
                Thread.sleep(sleepTime + randomGenerator.nextInt(50));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}

class ListSort {

    public static CopyOnWriteArrayList<Integer> mainNumberList = new CopyOnWriteArrayList<Integer>();
    public static int maxMainlistSize;

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
