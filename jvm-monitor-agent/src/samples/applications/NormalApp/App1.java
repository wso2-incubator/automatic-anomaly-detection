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
public class App1 {

    public static void main(String[] args) {

        int numbersOfThread = 8;
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < numbersOfThread; i++) {
            Runnable worker = new NumberBuilder(100, 100);
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
    private int maxMainlistSize = 100000;

    public synchronized void addtoMainList(List<Integer> numbers) {

        if (mainNumberList.size() > maxMainlistSize) {
            mainNumberList.clear();
        }

        mainNumberList.addAll(numbers);
        ListSort listSort = new ListSort();
        listSort.bubbleSort(mainNumberList);

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
