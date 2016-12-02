import java.util.ArrayList;
import java.util.Random;


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
public class NormalApp2 {


    public static void main(String[] args) throws InterruptedException {
        NormalApp2 executor = new NormalApp2();

        int len = 10;
        int size = 100000000;

        try {
            if (args.length == 1) {
                len = Integer.parseInt(args[0]);
            }

            if (args.length > 1) {
                len = Integer.parseInt(args[0]);
                size = Integer.parseInt(args[1]);
            }
        } catch (NumberFormatException e) {

        }


        executor.execute(len, size);
    }

    public void execute(int len, int size) {

        for (int i = 0; i < len; i++) {
            Thread program = new Thread(new Program(i + 1, size));
            program.start();
        }

    }

    public class Program implements Runnable {

        private int index;
        private int size;
        private Random random;

        Program(int index, int size) {
            this.index = index;
            this.size = size;
            this.random = new Random();

        }

        @Override
        public void run() {

            while (true) {

                int limit = random.nextInt(size - size / 2) + size / 2;

                for (int i = 0; i < limit; i++) {
                    int len = random.nextInt(size / 2);

                    if (len > 500000) {
                        len = 500000;
                    }
                    ArrayList<Long> arrayList = new ArrayList<Long>(len);

                    for (int k = 0; k < arrayList.size(); k++) {
                        Long number = random.nextLong();
                        arrayList.add(number);
                        System.out.println("Thread" + index + "- Number added to the list : " + number);

                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                int j = 0;
                while (j < limit) {

                    j++;
                    System.out.println("Thread" + index + "- count =" + j);

                    double x = Math.random();
                    double y = Math.random();
                    double z = Math.random();

                    double ans = x + y / z;
                    double ans2 = Math.exp(Math.sin(x * Math.PI) + 12);

                    System.out.println("Thread" + index + "- ANSWER = " + x + "-" + y + "/" + z);
                }
            }
        }
    }
}


