package samples.applications.NormalAppToOOM;/*
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
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * This is sample java app for test purpose.
 */
public class NormalAppToOOM {

    /**
     * This app generates 4 random size arrays with random values.
     * Parameters:-
     * <p>
     * Array Size
     * Max Integer
     * Fixed Value
     * Calculation Limit
     * <p>
     * Eg:- (1000000 1000000) or (1000000 1000000 1000000 10000000)
     *
     * @param args
     */
    public static void main(String[] args) {

        int arraySize = 1000000;
        int maxInteger = 1000000;
        int fixedValue = 1000000;
        int calLimit = 10000000;

        try {
            if (args.length == 2) {
                arraySize = Integer.parseInt(args[0]);
                maxInteger = Integer.parseInt(args[1]);
            } else if (args.length == 4) {
                arraySize = Integer.parseInt(args[0]);
                maxInteger = Integer.parseInt(args[1]);
                fixedValue = Integer.parseInt(args[2]);
                calLimit = Integer.parseInt(args[3]);
            }
        } catch (NumberFormatException e) {
            System.err.println(e);
        }

        NumberGenerator builder = new NumberGenerator(arraySize, maxInteger, fixedValue, calLimit);
        long normalAppRunTime = 60000;
        builder.run(normalAppRunTime);

    }
}


class NumberGenerator {

    final static Logger logger = Logger.getLogger(String.valueOf(NumberGenerator.class));

    Random randomGenerator = new Random();
    private int arraySize;
    private int maxInteger;
    private int calLimit;
    private int fixedValue;

    public NumberGenerator(int arraySize, int maxInteger, int fixedValue, int calLimit) {
        this.arraySize = arraySize;
        this.maxInteger = maxInteger;
        this.fixedValue = fixedValue;
        this.calLimit = calLimit;
    }

    public void run(long normalAppRunTime) {

        logger.info("Starting Number-generator");

        Date timeToRun = new Date(System.currentTimeMillis() + normalAppRunTime);
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            public void run() {
                logger.info("Generate OOM scenario");
                int numberOfThread = 1;

                for (int x = 0; x < numberOfThread; x++) {
                    Thread t = new Thread(new GenerateOOME());
                    t.start();
                }
            }
        }, timeToRun);

        while (true) {

            List<Integer> numbers = new ArrayList<Integer>(randomGenerator.nextInt(arraySize) + fixedValue);
            List<Integer> numbers2 = new ArrayList<Integer>(randomGenerator.nextInt(arraySize) + fixedValue);
            List<Integer> numbers3 = new ArrayList<Integer>(randomGenerator.nextInt(arraySize) + fixedValue);
            List<Integer> numbers4 = new ArrayList<Integer>(randomGenerator.nextInt(arraySize) + fixedValue);
            List<Integer> numbers5 = new ArrayList<Integer>(randomGenerator.nextInt(arraySize) + fixedValue);
            List<Integer> numbers6 = new ArrayList<Integer>(randomGenerator.nextInt(arraySize) + fixedValue);

            for (int x = 0; x < numbers.size(); x++) {
                numbers.add(randomGenerator.nextInt(maxInteger));
                numbers2.add(randomGenerator.nextInt(maxInteger));
                numbers3.add(randomGenerator.nextInt(maxInteger));
                numbers4.add(randomGenerator.nextInt(maxInteger));
                numbers5.add(randomGenerator.nextInt(maxInteger));
                numbers6.add(randomGenerator.nextInt(maxInteger));
            }
            numbers.clear();

            int i = 0;
            int cal = randomGenerator.nextInt(calLimit);
            double limit = 0;
            while (i < cal) {
                limit += Math.atan(Math.sqrt(i));
                i++;
            }

            try {
                Thread.sleep(randomGenerator.nextInt(300));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }


}

class GenerateOOME implements Runnable {

    final static Logger logger = Logger.getLogger(String.valueOf(GenerateOOME.class));

    public static int threadSleepTime = 0;
    private List<Integer> numbers = new ArrayList<Integer>();

    private void addNumber() {

        try {
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
        } catch (OutOfMemoryError e) {
            System.err.println(e);
            System.exit(1);
        }

    }

    public void run() {
        addNumber();
    }

}
