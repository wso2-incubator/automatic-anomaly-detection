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

/**
 * This class provide Out of Memory scenario
 */
public class BadCode {

    public static void main(String[] args) throws Exception {
        BadCode memoryTest = new BadCode();
        memoryTest.generateOOM();
    }

    public void generateOOM() throws Exception {
        Thread.sleep(5000);

        int iteratorValue = 10;
        System.out.println("\n=================> OOM test started..\n");
        for (int outerIterator = 1; outerIterator < 200; outerIterator++) {
            System.out.println(" 1 Iteration " + outerIterator + " Free Mem: " + Runtime.getRuntime().freeMemory());
            int loop1 = 2;
            int[] memoryFillIntVar = new int[iteratorValue];
// feel memoryFillIntVar array in loop..
            do {
                memoryFillIntVar[loop1] = 0;
                loop1--;
            } while (loop1 > 0);
            iteratorValue = (int) (iteratorValue * 1) + 100000;
            System.out.println("\nRequired Memory for next loop: " + iteratorValue);
            Thread.sleep(500);
        }
        Thread.sleep(5000);
        iteratorValue = 10;
        for (int outerIterator = 1; outerIterator < 200; outerIterator++) {
            System.out.println(" 2 Iteration " + outerIterator + " Free Mem: " + Runtime.getRuntime().freeMemory());
            int loop1 = 2;
            int[] memoryFillIntVar = new int[iteratorValue];
// feel memoryFillIntVar array in loop..
            do {
                memoryFillIntVar[loop1] = 0;
                loop1--;
            } while (loop1 > 0);
            iteratorValue = (int) (iteratorValue * 1) + 100000;
            System.out.println("\nRequired Memory for next loop: " + iteratorValue);
            Thread.sleep(500);
        }
        Thread.sleep(5000);
        iteratorValue = 10;
        for (int outerIterator = 1; outerIterator < 200; outerIterator++) {
            System.out.println(" 2 Iteration " + outerIterator + " Free Mem: " + Runtime.getRuntime().freeMemory());
            int loop1 = 2;
            int[] memoryFillIntVar = new int[iteratorValue];
// feel memoryFillIntVar array in loop..
            do {
                memoryFillIntVar[loop1] = 0;
                loop1--;
            } while (loop1 > 0);
            iteratorValue = (int) (iteratorValue * 1.1) + 100000;
            System.out.println("\nRequired Memory for next loop: " + iteratorValue);
            Thread.sleep(500);
        }


        Thread.sleep(10000);
    }


}
