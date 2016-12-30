/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.wso2.siddhi.extension.statistic;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.wso2.siddhi.core.ExecutionPlanRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;

public class XXSumFunctionExtensionTestCase {

    private static final Logger log = Logger.getLogger(XXSumFunctionExtensionTestCase.class);

    @Test
    public void timeWindowBatchTest1() throws InterruptedException {

        System.out.println("\nXX TestCase 1 (Double)");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "" +
                "define stream inputStream (time long, a double, b double);";
        String query = "" +
                "@info(name = 'query1') " +
                "from inputStream " +
                "select time, statistic:xx(a, b) as xx " +
                "insert into outputStream;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);

        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                for (Event ev : events) {
                    System.out.println("time: " + ev.getData()[0] + "\txx: " + ev.getData()[1]);
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{100l, 1d, 0d});
        inputHandler.send(new Object[]{101l, 2d, 1d});
        inputHandler.send(new Object[]{102l, 3d, 3d});

        executionPlanRuntime.shutdown();
    }

    @Test
    public void timeWindowBatchTest2() throws InterruptedException {

        System.out.println("\nXX TestCase 2 (Long)");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "" +
                "define stream inputStream (time long, a long, b long);";
        String query = "" +
                "@info(name = 'query1') " +
                "from inputStream " +
                "select time, statistic:xx(a, b) as xX " +
                "insert into outputStream;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);

        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                for (Event ev : events) {
                    System.out.println("time: " + ev.getData()[0] + "\txx: " + ev.getData()[1]);
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{100l, 1l, 0l});
        inputHandler.send(new Object[]{101l, 2l, 1l});
        inputHandler.send(new Object[]{102l, 3l, 3l});

        executionPlanRuntime.shutdown();
    }

    @Test
    public void timeWindowBatchTest3() throws InterruptedException {

        System.out.println("\nXX TestCase 3 (Integer)");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "" +
                "define stream inputStream (time long, a int, b int);";
        String query = "" +
                "@info(name = 'query1') " +
                "from inputStream " +
                "select time, statistic:xx(a, b) as xx " +
                "insert into outputStream;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);

        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                for (Event ev : events) {
                    System.out.println("time: " + ev.getData()[0] + "\txx: " + ev.getData()[1]);
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{100l, 1, 0});
        inputHandler.send(new Object[]{101l, 2, 1});
        inputHandler.send(new Object[]{102l, 3, 3});

        executionPlanRuntime.shutdown();
    }

    @Test
    public void timeWindowBatchTest4() throws InterruptedException {

        System.out.println("\nXX TestCase 4 (Float)");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "" +
                "define stream inputStream (time long, a float, b float);";
        String query = "" +
                "@info(name = 'query1') " +
                "from inputStream " +
                "select time, statistic:xx(a, b) as xx " +
                "insert into outputStream;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);

        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                for (Event ev : events) {
                    System.out.println("time: " + ev.getData()[0] + "\txx: " + ev.getData()[1]);
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{100l, 1f, 0f});
        inputHandler.send(new Object[]{101l, 2f, 1f});
        inputHandler.send(new Object[]{102l, 3f, 3f});

        executionPlanRuntime.shutdown();
    }

//    @Test
//    public void timeWindowBatchTest5() throws InterruptedException {
//
//        System.out.println("\nTestCase 5 (Real Data)");
//        SiddhiManager siddhiManager = new SiddhiManager();
//
//        String inputStream = "" +
//                "define stream inputStream (time long, min double, max double, n int, data double);";
//        String query = "" +
//                "@info(name = 'query1') " +
//                "from inputStream#window.time(50) " +
//                "select time, statistic:prob(min, max, n, data) as Probability " +
//                "insert into outputStream ;";
//
//        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);
//
//        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
//            @Override
//            public void receive(org.wso2.siddhi.core.event.Event[] events) {
//                for (Event ev : events) {
//                    System.out.println("time: " + ev.getData()[0] + "\tProbability: " + ev.getData()[1]);
//                }
//            }
//        });
//
//        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
//        executionPlanRuntime.start();
//
//        inputHandler.send(new Object[]{1477030450161l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 9.354088127040046E-5d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030451168l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.27680798004987534d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030452176l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.2524752475247525d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030453182l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.28967254408060455d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030454192l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.26119402985074625d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030455198l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.26515151515151514d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030456203l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.2817258883248731d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030457209l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.255050505050505d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030458216l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.2810126582278481d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030459221l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.2759493670886076d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030460227l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.2525d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030461235l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.2831168831168831d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030462239l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.27341772151898736d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030463245l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.2582278481012658d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030464249l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.2695214105793451d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030465255l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.27455919395465994d});
//        Thread.sleep(10);
//
//        executionPlanRuntime.shutdown();
//    }
//
//    @Test
//    public void timeWindowBatchTest6() throws InterruptedException {
//
//        System.out.println("\nTestCase 6 (Real Data)");
//        SiddhiManager siddhiManager = new SiddhiManager();
//
//        String inputStream = "" +
//                "define stream inputStream (time long, min double, max double, n int, data double);";
//        String query = "" +
//                "@info(name = 'query1') " +
//                "from inputStream#window.time(50) " +
//                "select time, statistic:prob(min, max, n, data) as Probability " +
//                "insert into outputStream ;";
//
//        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);
//
//        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
//            @Override
//            public void receive(org.wso2.siddhi.core.event.Event[] events) {
//                for (Event ev : events) {
//                    System.out.println("time: " + ev.getData()[0] + "\tProbability: " + ev.getData()[1]);
//                }
//            }
//        });
//
//        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
//        executionPlanRuntime.start();
//
//        inputHandler.send(new Object[]{1477030450161l, 2.5968243989815046E-5d, 0.28967254408060455d, 3, 9.354088127040046E-5d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030451168l, 2.5968243989815046E-5d, 0.28967254408060455d, 3, 0.27680798004987534d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030452176l, 2.5968243989815046E-5d, 0.28967254408060455d, 3, 0.2524752475247525d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030453182l, 2.5968243989815046E-5d, 0.28967254408060455d, 3, 0.28967254408060455d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030454192l, 2.5968243989815046E-5d, 0.28967254408060455d, 3, 0.26119402985074625d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030455198l, 2.5968243989815046E-5d, 0.28967254408060455d, 3, 0.26515151515151514d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030456203l, 2.5968243989815046E-5d, 0.28967254408060455d, 3, 0.2817258883248731d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030457209l, 2.5968243989815046E-5d, 0.28967254408060455d, 3, 0.255050505050505d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030458216l, 2.5968243989815046E-5d, 0.28967254408060455d, 3, 0.2810126582278481d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030459221l, 2.5968243989815046E-5d, 0.28967254408060455d, 3, 0.2759493670886076d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030460227l, 2.5968243989815046E-5d, 0.28967254408060455d, 3, 0.2525d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030461235l, 2.5968243989815046E-5d, 0.28967254408060455d, 3, 0.2831168831168831d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030462239l, 2.5968243989815046E-5d, 0.28967254408060455d, 3, 0.27341772151898736d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030463245l, 2.5968243989815046E-5d, 0.28967254408060455d, 3, 0.2582278481012658d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030464249l, 2.5968243989815046E-5d, 0.28967254408060455d, 3, 0.2695214105793451d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030465255l, 2.5968243989815046E-5d, 0.28967254408060455d, 3, 0.27455919395465994d});
//        Thread.sleep(10);
//
//        executionPlanRuntime.shutdown();
//    }
//
//
//    @Test
//    public void timeWindowBatchTest7() throws InterruptedException {
//
//        System.out.println("TestCase 7 (Double)");
//        SiddhiManager siddhiManager = new SiddhiManager();
//
//        String inputStream = "" +
//                "define stream inputStream (time long, min double, max double, n int, data double);";
//        String query = "" +
//                "@info(name = 'query1') " +
//                "from inputStream#window.time(30) " +
//                "select time, statistic:prob(min, max, n, data) as Probability " +
//                "insert into outputStream ;";
//
//        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);
//
//        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
//            @Override
//            public void receive(org.wso2.siddhi.core.event.Event[] events) {
//                for (Event ev : events) {
//                    System.out.println("time: " + ev.getData()[0] + "\tProbability: " + ev.getData()[1]);
//                }
//            }
//        });
//
//        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
//        executionPlanRuntime.start();
//
//        inputHandler.send(new Object[]{100l, 200d, 500d, 3, 200d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{101l, 100d, 500d, 3, 350d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{102l, 100d, 700d, 3, 550d});
//        Thread.sleep(12);
//        inputHandler.send(new Object[]{100l, 100d, 700d, 3, 160d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{101l, 0d, 700d, 3, 380d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{102l, 10d, 500d, 3, 15d});
//        Thread.sleep(12);
//        inputHandler.send(new Object[]{100l, 0d, 700d, 3, 152d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{101l, 0d, 800d, 3, 401d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{102l, 0d, 800d, 3, 23d});
//        Thread.sleep(12);
//        inputHandler.send(new Object[]{100l, 0d, 800d, 3, 778d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{101l, 0d, 800d, 3, 703d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{102l, 0d, 800d, 3, 723d});
//        Thread.sleep(12);
//
//
//        executionPlanRuntime.shutdown();
//    }
//
//    @Test
//    public void timeWindowBatchTest8() throws InterruptedException {
//
//        System.out.println("\nTestCase 8 (Real Data)");
//        SiddhiManager siddhiManager = new SiddhiManager();
//
//        String inputStream = "" +
//                "define stream inputStream (time long, min double, max double, n int, data double);";
//        String query = "" +
//                "@info(name = 'query1') " +
//                "from inputStream#window.time(30) " +
//                "select time, statistic:prob(min, max, n, data) as Probability " +
//                "insert into outputStream ;";
//
//        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);
//
//        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
//            @Override
//            public void receive(org.wso2.siddhi.core.event.Event[] events) {
//                for (Event ev : events) {
//                    System.out.println("time: " + ev.getData()[0] + "\tProbability: " + ev.getData()[1]);
//                }
//            }
//        });
//
//        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
//        executionPlanRuntime.start();
//
//        inputHandler.send(new Object[]{1477030450161l, 9.354088127040046E-3, 0.2524752475247525d, 10,  9.354088127040047E-3d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030451168l, 2.5968243989815043E-3d, 0.2524752475247525d, 10,  2.5968243989815047E-3d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030452176l, 2.5968243989815050E-5d, 0.2524752475247525d, 10,  0.2524752475247525d});
//        Thread.sleep(12);
//        inputHandler.send(new Object[]{1477030453182l, 2.5968243989815050E-5d,  0.28967254408060455d, 10, 0.27680798004987534d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030454192l, 2.5968243989815050E-5d, 0.27680798004987534d, 10,  0.26119402985074625d});
//        Thread.sleep(10);
//        inputHandler.send(new Object[]{1477030455198l, 2.5068243989815046E-5d, 0.2810126582278481d, 10,  0.26515151515151514d});
//        Thread.sleep(12);
//        inputHandler.send(new Object[]{1477030456203l, 2.5068243989815046E-5d, 0.2817258883248731d, 10,  0.2810126582278481d});
//        Thread.sleep(10);
//
//        executionPlanRuntime.shutdown();
//    }

}
