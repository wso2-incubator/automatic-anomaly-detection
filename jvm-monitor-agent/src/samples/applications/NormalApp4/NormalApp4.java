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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is sample java app for test purpose
 */
public class NormalApp4 {

    /**
     * Parameters:-
     * <p>
     * Palindrome number upper bound
     * Thread 1 sleep time (ms)
     * Prime number upper bound
     * Thread 2 sleep time (ms)
     * Large Prime number
     * Thread 3 sleep time (ms)
     * <p>
     * Eg:- (1000000 1500 2000000 2500 600851475143 4000)
     */
    public static void main(String[] args) {

        //Create thread pool
        ExecutorService executor = Executors.newFixedThreadPool(3);

        LargestPalindrome palindrome = new LargestPalindrome();
        PrimeSum prime = new PrimeSum();
        LargestPrimeFactor largestPrimeFactor = new LargestPrimeFactor();

        Runnable worker[] = new Runnable[3];
        worker[0] = palindrome;
        worker[1] = prime;
        worker[2] = largestPrimeFactor;

        if (args.length == 3) {

            try {
                palindrome.setLimit(Integer.parseInt(args[0]));
                prime.setPrimeLimit(args[1]);
                largestPrimeFactor.setNumber(args[2]);
            } catch (NumberFormatException e) {
                System.err.println(e);
            }

        } else if (args.length == 6) {

            try {
                palindrome.setLimit(Integer.parseInt(args[0]));
                palindrome.setThreadSleepTime(Integer.parseInt(args[1]));
                prime.setPrimeLimit(args[2]);
                prime.setThreadSleepTime(Integer.parseInt(args[3]));
                largestPrimeFactor.setNumber(args[4]);
                largestPrimeFactor.setThreadSleepTime(Integer.parseInt(args[5]));
            } catch (NumberFormatException e) {
                System.err.println(e);
            }

        }

        for (Runnable work : worker) {
            executor.execute(work);
        }

        executor.shutdown();
        while (!executor.isTerminated()) ;

    }


}

class LargestPalindrome implements Runnable {

    private List<Integer> palindrome = new ArrayList<Integer>();
    private int limit = 1000000;
    private int threadSleepTime = 1500;

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setThreadSleepTime(int threadSleepTime) {
        this.threadSleepTime = threadSleepTime;
    }

    private void findPalindrom() {

        int palindromicNumber = 0;
        int number1 = 0;
        int number2 = 0;

        for (int m = 100; m < limit; m++) {
            for (int n = m + 1; n < limit; n++) {
                String cp = n * m + "";
                boolean pa = true;

                for (int c = 1; c <= cp.length() / 2; c++) {
                    if (!cp.substring(c - 1, c).equals(cp.substring(cp.length() - c, cp.length() - c + 1))) {
                        pa = false;
                        break;
                    }
                }
                if (pa == true && palindromicNumber < n * m) {
                    palindromicNumber = m * n;
                    number1 = m;
                    number2 = n;
                }
            }

            try {
                Thread.sleep(threadSleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

//        System.out.println("The Largest Palindrome made from the product of two"
//                + " 3-digit numbers is " + palindromicNumber + " = " + number1 + " x " + number2);

    }

    public void run() {
        findPalindrom();
    }
}

class PrimeSum implements Runnable {

    String primeLimit = "2000000";
    private int threadSleepTime = 2500;

    public void setPrimeLimit(String primeLimit) {
        this.primeLimit = primeLimit;
    }

    public void setThreadSleepTime(int threadSleepTime) {
        this.threadSleepTime = threadSleepTime;
    }

    public void run() {

        BigInteger m;
        BigInteger sumOfPrime = new BigInteger("0");

        for (m = new BigInteger("2"); m.compareTo(new BigInteger(primeLimit)) < 0; m = m.add(BigInteger.ONE)) {
            int cou = 0;
            BigInteger n;
            for (n = new BigInteger("2"); n.compareTo(m) < 0; n = n.add(BigInteger.ONE)) {
                if (m.remainder(n).compareTo(BigInteger.ZERO) == 0) {
                    cou++;
                }
                if (cou > 0) {
                    break;
                }
            }

            if (cou == 0) {
                sumOfPrime = sumOfPrime.add(m);
            }

            try {
                Thread.sleep(threadSleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

//        System.out.println("Sum of all the Primes Below Two Million = " + sumOfPrime);

    }
}

class LargestPrimeFactor implements Runnable {

    String number = "600851475143";
    private int threadSleepTime = 4000;

    public void setThreadSleepTime(int threadSleepTime) {
        this.threadSleepTime = threadSleepTime;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void run() {

        int cou = 0;
        BigInteger m, largestPrimeFactor = new BigInteger("0"), num = new BigInteger(number);

        for (m = new BigInteger("2"); m.compareTo(num) <= 0; m = m.add(BigInteger.ONE)) {

            cou++;
            if (num.remainder(m).compareTo(BigInteger.ZERO) == 0) {
                cou = 0;
                for (BigInteger n = new BigInteger("2"); n.compareTo(m) < 0; n = n.add(BigInteger.ONE)) {
                    if (m.remainder(n).compareTo(BigInteger.ZERO) == 0) {
                        cou++;
                        break;
                    }
                }

                if (cou == 0) {
                    largestPrimeFactor = m;
                }
            }

            try {
                Thread.sleep(threadSleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

//        System.out.println("Largest Prime Factor of the " + number + " = " + largestPrimeFactor);

    }
}
