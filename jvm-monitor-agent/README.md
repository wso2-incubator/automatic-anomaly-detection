# JVM Monitor Agent(JMA)

## Introduction
The JVM Monitoring Agent(JMA) is a WSO2 DAS based JVM system metric usage monitoring tool which is capable of publishing those data into WSO2 DAS.

The types of usage data collected are;

1. Memory Usage Data (Heap and Non Heap)

2. CPU Load Percentages (JVM process and System)

3. Garbage Collection Logs (GC Type, Memory parameters before and after GC)

## Prerequisites

- [WSO2 DAS 3.1.0](http://wso2.com/products/data-analytics-server/) or later (Using lower version of DAS may required WSO2 ML and WSO2 CEP products)
- [WSO2 Anomaly Detection DAS Configuration Resources](https://github.com/wso2-incubator/automatic-anomaly-detection/tree/master/wso2-das)

## Installation

1. **JVM Monitor Agent** is built using Apache Maven. To build JMA, run:

        <JMA_HOME>/ mvn clean install

2. Configure DAS using [**WSO2 Anomaly Detection DAS Configuration Resources**](https://github.com/wso2-incubator/automatic-anomaly-detection/tree/master/wso2-das)

## Deployment

1. [Run WSO2 DAS server](https://docs.wso2.com/display/DAS310/Running+the+Product)

2. Open the terminal, Navigate to \<PROJECT_HOME> and use the following command: (***Note:*** Need to have built the project using ```mvn clean install``` before doing this)

        cp -i jvm-monitor-agent/target/jvm-monitor-agent-1.0-SNAPSHOT-jar-with-dependencies.jar .
        java -cp $JAVA_HOME/lib/tools.jar:jvm-monitor-agent-1.0-SNAPSHOT-jar-with-dependencies.jar controller.JVMMonitorAgent <Input arguments>

    ***\<Input arguments> should be in following order:***
    **Monitoring application in remote server using JMX URL**
    ```
    -u [jmx_url] [username]<optional> [password]<optional>
    ```
    |Argument| Description|
    |--------| -----------|
    |**[jmx_url]**| JMX URL* of the remote server |
    |**username**| Username of the JMX service |
    |**password**| Password of the JMX service |
    
    \* service:jmx:rmi://\<TARGET_MACHINE>:\<JMX_RMI_SERVER_PORT>/jndi/rmi://\<TARGET_MACHINE>:\<RMI_REGISTRY_PORT>/jmxrmi
        
    **Monitoring application in same machine**
    ```
    [app_name] [app_path] [app_args] [options]
    ```


    | Arguments | Description|
    |------------|------------|
    |***[app_name]***| Monitored application name without file extension |
    |***[app_path]***| Monitored application path relative to \<JMA_HOME> |
    |***[app_args]***| Monitored application arguments. For sample applications please refere [README.md](https://github.com/wso2-incubator/automatic-anomaly-detection/tree/master/jvm-monitor-agent/src/samples/applications)|
    |***[options]*** | Use to control the JMA (Please refer options table for more details) |

    | Options | Description |
    |---------|-------------|
    | ***-j***| If a jar file is used as application |
    | ***-a***| If absolute file path is given instead of relative path |
    | ***-f***| If .class file is to be run from the given file path. (does not compile monitoring application) (if “-f” is not given, particular .java file will first compile then run) |
    | ***-r***| If respected monitoring application process is terminated, then it would run again automatically and continue monitoring |
