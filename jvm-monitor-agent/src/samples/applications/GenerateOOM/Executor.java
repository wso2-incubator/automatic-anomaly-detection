/*
*  Copyright (c) ${date}, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

public class Executor {

    public static void main(String[] args) throws Exception {
        Thread.sleep(5000);

        double x = 1;
        int y = 10000;

        if (args.length == 1){
            x = Double.parseDouble(args[0]);
        }
        if (args.length > 1) {
            x = Double.parseDouble(args[0]);
            y = Integer.parseInt(args[1]);
        }


        generateOOM(x,y);
    }


    /**
     * This method generate Out of memory exception eventually
     *
     * Generate int arrays with growing length
     *
     * @param multiplier
     * @param addition
     * @throws Exception
     */
    static void generateOOM(double multiplier , int addition) throws Exception {

        int iteratorValue = 10;

        for (int outerIterator = 1; outerIterator < 20000; outerIterator++) {

            int loop1 = 2;
            int[] memoryFillIntVar = new int[iteratorValue];

            // feel memoryFillIntVar array in loop..
            do {
                memoryFillIntVar[loop1] = 0;
                loop1--;
            } while (loop1 > 0);

            //increase the length of next array
            iteratorValue = (int)(iteratorValue * multiplier) + addition;

            Thread.sleep(500);
        }
    }


}
