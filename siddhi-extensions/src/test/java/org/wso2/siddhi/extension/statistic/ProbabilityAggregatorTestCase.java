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

public class ProbabilityAggregatorTestCase {

    private static final Logger log = Logger.getLogger(ProbabilityAggregatorTestCase.class);

    @Before
    public void init() {
    }

    @Test
    public void timeWindowBatchTest0() throws InterruptedException {

        System.out.println("TestCase 0 (Real Data - Double)");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "" +
                "define stream CPUUsageStream(time_stamp long, process_cpu_load double);";

        String query = "@info(name = 'query1') " +
                "from CPUUsageStream#window.time(50) " +
                "select " +
                "    time_stamp," +
                "    statistic:prob(0.08179419525065963d, 0.2518891687657431d, 10, process_cpu_load) as p_process_cpu_load " +
                "insert into CPUFeatures;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);

        executionPlanRuntime.addCallback("CPUFeatures", new StreamCallback() {
            @Override
            public void receive(org.wso2.siddhi.core.event.Event[] events) {
                for (Event ev : events) {
                    System.out.println("time_stamp: " + ev.getData()[0] + "\tp_process_cpu_load: " + ev.getData()[1]);
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("CPUUsageStream");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{1477030451168l, 0.27680798004987534d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030452176l, 0.2524752475247525d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030453182l, 0.28967254408060455d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030454192l, 0.26119402985074625d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030455198l, 0.26515151515151514d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030456203l, 0.2817258883248731d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030457209l, 0.255050505050505d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030458216l, 0.2810126582278481d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030459221l, 0.2759493670886076d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030460227l, 0.2525d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030461235l, 0.2831168831168831d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030462239l, 0.27341772151898736d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030463245l, 0.2582278481012658d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030464249l, 0.2695214105793451d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030465255l, 0.27455919395465994d});
        Thread.sleep(10);

        executionPlanRuntime.shutdown();
    }


    @Test
    public void timeWindowBatchTest1() throws InterruptedException {

        System.out.println("\nTestCase 1 (Double)");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "" +
                "define stream inputStream (time long, min double, max double, n int, data double);";
        String query = "" +
                "@info(name = 'query1') " +
                "from inputStream#window.time(50) " +
                "select time, statistic:prob(min, max, n, data) as Probability " +
                "insert into outputStream ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);

        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
            @Override
            public void receive(org.wso2.siddhi.core.event.Event[] events) {
                for (Event ev : events) {
                    System.out.println("time: " + ev.getData()[0] + "\tProbability: " + ev.getData()[1]);
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{100l, 0d, 1000d, 3, 50d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{101l, 0d, 1000d, 3, 1d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{102l, 0d, 1000d, 3, 405d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{103l, 0d, 1000d, 3, 505d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{104l, 0d, 1000d, 3, 700d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{105l, 0d, 1000d, 3, 558d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{106l, 0d, 1000d, 3, 880d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{107l, 0d, 1000d, 3, 508d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{108l, 0d, 1000d, 3, 503d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{109l, 0d, 1000d, 3, 332d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{110l, 0d, 1000d, 3, 232d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{111l, 0d, 1000d, 3, 123d});


        executionPlanRuntime.shutdown();
    }

    @Test
    public void timeWindowBatchTest2() throws InterruptedException {

        System.out.println("\nTestCase 2 (Long)");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "" +
                "define stream inputStream (time long, min long, max long, n int, data long);";
        String query = "" +
                "@info(name = 'query1') " +
                "from inputStream#window.time(50) " +
                "select time, statistic:prob(min, max, n, data) as Probability " +
                "insert into outputStream ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);

        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
            @Override
            public void receive(org.wso2.siddhi.core.event.Event[] events) {
                for (Event ev : events) {
                    System.out.println("time: " + ev.getData()[0] + "\tProbability: " + ev.getData()[1]);
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{100l, 0l, 1000l, 3, 50l});
        Thread.sleep(10);
        inputHandler.send(new Object[]{101l, 0l, 1000l, 3, 1l});
        Thread.sleep(10);
        inputHandler.send(new Object[]{102l, 0l, 1000l, 3, 405l});
        Thread.sleep(10);
        inputHandler.send(new Object[]{103l, 0l, 1000l, 3, 505l});
        Thread.sleep(10);
        inputHandler.send(new Object[]{104l, 0l, 1000l, 3, 700l});
        Thread.sleep(10);
        inputHandler.send(new Object[]{105l, 0l, 1000l, 3, 558l});
        Thread.sleep(10);
        inputHandler.send(new Object[]{106l, 0l, 1000l, 3, 880l});
        Thread.sleep(10);
        inputHandler.send(new Object[]{107l, 0l, 1000l, 3, 508l});
        Thread.sleep(10);
        inputHandler.send(new Object[]{108l, 0l, 1000l, 3, 503l});
        Thread.sleep(10);
        inputHandler.send(new Object[]{109l, 0l, 1000l, 3, 332l});
        Thread.sleep(10);
        inputHandler.send(new Object[]{110l, 0l, 1000l, 3, 232l});
        Thread.sleep(10);
        inputHandler.send(new Object[]{111l, 0l, 1000l, 3, 123l});


        executionPlanRuntime.shutdown();
    }

    @Test
    public void timeWindowBatchTest3() throws InterruptedException {

        System.out.println("\nTestCase 3 (INTEGER)");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "" +
                "define stream inputStream (time long, min int, max int, n int, data int);";
        String query = "" +
                "@info(name = 'query1') " +
                "from inputStream#window.time(50) " +
                "select time, statistic:prob(min, max, n, data) as Probability " +
                "insert into outputStream ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);

        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
            @Override
            public void receive(org.wso2.siddhi.core.event.Event[] events) {
                for (Event ev : events) {
                    System.out.println("time: " + ev.getData()[0] + "\tProbability: " + ev.getData()[1]);
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{100l, 0, 1000, 3, 50});
        Thread.sleep(10);
        inputHandler.send(new Object[]{101l, 0, 1000, 3, 1});
        Thread.sleep(10);
        inputHandler.send(new Object[]{102l, 0, 1000, 3, 405});
        Thread.sleep(10);
        inputHandler.send(new Object[]{103l, 0, 1000, 3, 505});
        Thread.sleep(10);
        inputHandler.send(new Object[]{104l, 0, 1000, 3, 700});
        Thread.sleep(10);
        inputHandler.send(new Object[]{105l, 0, 1000, 3, 558});
        Thread.sleep(10);
        inputHandler.send(new Object[]{106l, 0, 1000, 3, 880});
        Thread.sleep(10);
        inputHandler.send(new Object[]{107l, 0, 1000, 3, 508});
        Thread.sleep(10);
        inputHandler.send(new Object[]{108l, 0, 1000, 3, 503});
        Thread.sleep(10);
        inputHandler.send(new Object[]{109l, 0, 1000, 3, 332});
        Thread.sleep(10);
        inputHandler.send(new Object[]{110l, 0, 1000, 3, 232});
        Thread.sleep(10);
        inputHandler.send(new Object[]{111l, 0, 1000, 3, 123});


        executionPlanRuntime.shutdown();
    }

    @Test
    public void timeWindowBatchTest4() throws InterruptedException {

        System.out.println("\nTestCase 4 (FLOAT)");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "" +
                "define stream inputStream (time long, min float, max float, n int, data float);";
        String query = "" +
                "@info(name = 'query1') " +
                "from inputStream#window.time(50) " +
                "select time, statistic:prob(min, max, n, data) as Probability " +
                "insert into outputStream ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);

        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
            @Override
            public void receive(org.wso2.siddhi.core.event.Event[] events) {
                for (Event ev : events) {
                    System.out.println("time: " + ev.getData()[0] + "\tProbability: " + ev.getData()[1]);
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{100l, 0f, 1000f, 3, 50f});
        Thread.sleep(10);
        inputHandler.send(new Object[]{101l, 0f, 1000f, 3, 1f});
        Thread.sleep(10);
        inputHandler.send(new Object[]{102l, 0f, 1000f, 3, 405f});
        Thread.sleep(10);
        inputHandler.send(new Object[]{103l, 0f, 1000f, 3, 505f});
        Thread.sleep(10);
        inputHandler.send(new Object[]{104l, 0f, 1000f, 3, 700f});
        Thread.sleep(10);
        inputHandler.send(new Object[]{105l, 0f, 1000f, 3, 558f});
        Thread.sleep(10);
        inputHandler.send(new Object[]{106l, 0f, 1000f, 3, 880f});
        Thread.sleep(10);
        inputHandler.send(new Object[]{107l, 0f, 1000f, 3, 508f});
        Thread.sleep(10);
        inputHandler.send(new Object[]{108l, 0f, 1000f, 3, 503f});
        Thread.sleep(10);
        inputHandler.send(new Object[]{109l, 0f, 1000f, 3, 332f});
        Thread.sleep(10);
        inputHandler.send(new Object[]{110l, 0f, 1000f, 3, 232f});
        Thread.sleep(10);
        inputHandler.send(new Object[]{111l, 0f, 1000f, 3, 123f});


        executionPlanRuntime.shutdown();
    }

    @Test
    public void timeWindowBatchTest5() throws InterruptedException {

        System.out.println("\nTestCase 5 (Real Data)");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "" +
                "define stream inputStream (time long, min double, max double, n int, data double);";
        String query = "" +
                "@info(name = 'query1') " +
                "from inputStream#window.time(50) " +
                "select time, statistic:prob(min, max, n, data) as Probability " +
                "insert into outputStream ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);

        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
            @Override
            public void receive(org.wso2.siddhi.core.event.Event[] events) {
                for (Event ev : events) {
                    System.out.println("time: " + ev.getData()[0] + "\tProbability: " + ev.getData()[1]);
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{1477030451168l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.27680798004987534d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030452176l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.2524752475247525d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030453182l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.28967254408060455d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030454192l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.26119402985074625d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030455198l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.26515151515151514d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030456203l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.2817258883248731d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030457209l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.255050505050505d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030458216l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.2810126582278481d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030459221l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.2759493670886076d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030460227l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.2525d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030461235l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.2831168831168831d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030462239l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.27341772151898736d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030463245l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.2582278481012658d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030464249l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.2695214105793451d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030465255l, 2.5968243989815046E-5d, 0.28967254408060455d, 10, 0.27455919395465994d});
        Thread.sleep(10);

        executionPlanRuntime.shutdown();
    }

    @Test
    public void timeWindowBatchTest6() throws InterruptedException {

        System.out.println("\nTestCase 6 (Real Data)");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "" +
                "define stream inputStream (time long, min double, max double, n int, data double);";
        String query = "" +
                "@info(name = 'query1') " +
                "from inputStream#window.time(30) " +
                "select time, statistic:prob(min, max, n, data) as Probability " +
                "insert into outputStream ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);

        executionPlanRuntime.addCallback("outputStream", new StreamCallback() {
            @Override
            public void receive(org.wso2.siddhi.core.event.Event[] events) {
                for (Event ev : events) {
                    System.out.println("time: " + ev.getData()[0] + "\tProbability: " + ev.getData()[1]);
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{1477030450161l, 9.354088127040046E-3, 0.2524752475247525d, 10, 0.1424752475247525d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030451168l, 2.5968243989815043E-3d, 0.2524752475247525d, 10, 0.2024752475247525d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030452176l, 2.5968243989815050E-5d, 0.2524752475247525d, 10, 0.2524752475247525d});
        Thread.sleep(12);
        inputHandler.send(new Object[]{1477030453182l, 2.5968243989815050E-5d, 0.28967254408060455d, 10, 0.27680798004987534d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030454192l, 2.5968243989815050E-5d, 0.27680798004987534d, 10, 0.26119402985074625d});
        Thread.sleep(10);
        inputHandler.send(new Object[]{1477030455198l, 2.5068243989815046E-5d, 0.2810126582278481d, 10, 0.26515151515151514d});
        Thread.sleep(12);
        inputHandler.send(new Object[]{1477030456203l, 2.5068243989815046E-5d, 0.2817258883248731d, 10, 0.2810126582278481d});
        Thread.sleep(10);

        executionPlanRuntime.shutdown();
    }


}
