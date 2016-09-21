# JVM Monitor Agent(JMA)

## Introdution
The JVM Monitoring Agent(JMA) is a WSO2 DAS based JVM system metric usage monitoring tool which is capable of publishing those data into WSO2 DAS.

The types of usage data collected are;

1. Memory Usage Data ( Heap and Non Heap)

2. CPU Load Percentages (JVM process and System)

3. Garbage Collection Logs ( GC Type, Memory parameters before and after GC)

## Prerequisites

- [WSO2 DAS 3.1.0](http://wso2.com/products/data-analytics-server/) or later (Using lower version of DAS may required WSO2 ML and WSO2 CEP products)
- [WSO2 Anomaly Detection DAS Configuration Resources](https://github.com/wso2-incubator/automatic-anomaly-detection/tree/master/wso2-das)

## Installation

1. **JVM Monitor Agent** is built using Apache Maven. To build JMA, run:

        <JMA_HOME>/ mvn clean install

2. Configure DAS using [**WSO2 Anomaly Detection DAS Configuration Resources**](https://github.com/wso2-incubator/automatic-anomaly-detection/tree/master/wso2-das)

## Deployment

1. [Run WSO2 DAS server](https://docs.wso2.com/display/DAS310/Running+the+Product)

2. Open the terminal and run:

    ```sh
        $ mvn exec:java
    ```

3. Or Use following command:
    ```sh
        $ mvn compile
        $ mvn package
        $ java -cp target/jvm-monitor-agent-1.0-SNAPSHOT.jar controller.Test <Input argument>
    ```

    **Input argument should be in following order:**

    **[** monitored application name **]** **[** file path **]** **[** string arguments **]** **[** options **]**

    ***---Description---***

    **[** monitored application name **]:** Monitored application name. Do not put file extension.

    **[** file path **]:**				            Need to give relative file path according to <JMA_HOME>

    **[** string arguments **]:**	 		        This depends on monitored application. If you run < JMA_HOME>/samples/applications/< APP_NAME> , please use “README.md” inside the particular directory.

    **[** options **]:**

	    -j 	     If a jar file is used.
	    -a	     If absolute file path is used.
	    -f 	     If .class file is to be run from the given file path. (does not compile monitoring application) (if “-f” is not given, particular .java file will first compile then run)
	    -r       If respected monitoring application process is terminated, then it would run again automatically and continue monitoring
