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

/**
 * This is sample java app for test purpose.
 */
public class NormalApp3 {


    /**
     * This is calculates primes up to user input.
     * Parameters:-
     * <p>
     * Upper bound of primes
     * GC force integer
     * <p>
     * Eg:- (1000000) or (1000000, 12)
     *
     * @param args
     */
    public static void main(String[] args) {

        int value = 10000000;
        int gcForceInt = 12;

        try {

            if (args.length == 1) {
                value = Integer.parseInt(args[0]);
            } else if (args.length == 2) {
                value = Integer.parseInt(args[0]);
                gcForceInt = Integer.parseInt(args[1]);
            }

        } catch (NumberFormatException e) {
            System.err.println(e);
        }

        Random randomGenerator = new Random();

        while (true) {
            (new Prime()).find(randomGenerator.nextInt(value), gcForceInt);
        }

    }


}

class Prime {

    private List<Integer> nonPrimes = new ArrayList<Integer>();

    public void find(int value, int gcForceInt) {

        long i = 1;

        while (i < value) {
            boolean isPrime = true;
            long j = 2, limit = (long) Math.sqrt(i);
            while (j <= limit) {
                if (i % j == 0) {
                    isPrime = false;
                    break;
                }
                j++;
            }

            if (!isPrime) {
                nonPrimes.add((int) i);
            }
            i++;
        }

        nonPrimes.clear();
        nonPrimes = new ArrayList<Integer>(3);

        //Forced to collect GC
        if (value % gcForceInt == 0) {
            System.gc();
        }

    }
}
