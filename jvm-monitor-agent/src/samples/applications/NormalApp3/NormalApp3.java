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
 * This is sample java app for test purpose.
 */
public class NormalApp3 {

    private List<Integer> primes = new ArrayList<Integer>();

    /**
     * This is calculates primes up to user input.
     * Parameters:-
     * <p>
     * Upper bound of primes
     * <p>
     * Eg:- (1000000)
     *
     * @param args
     */
    public static void main(String[] args) {

        long value = 1000000;

        if (args.length == 1) {

            try {
                value = Long.parseLong(args[0]);
            } catch (NumberFormatException e) {
                System.err.println(e);
            }

        }

        while (true) {
            NormalApp3 obj = new NormalApp3();
            obj.primeNumber(value);
        }

    }

    public void primeNumber(long value) {

        long i = 1;
        boolean isPrime;
        long j, limit;
        while (i < value) {
            isPrime = true;
            j = 2;
            limit = (long) Math.sqrt(i);
            while (j <= limit) {
                if (i % j == 0) {
                    isPrime = false;
                    break;
                }
                j++;
            }

            if (isPrime) {
                primes.add((int) i);
            }
            i++;
        }

        primes.clear();

    }
}
