#JVM Monitor Agent(JMA)

##Introdution 
The JVM Monitoring Agent(JMA) is a WSO2 DAS based JVM system metric usage monitoring tool which is capable of publishing those data into WSO2 DAS.

The types of usage data collected are;

1. Memory Usage Data ( Heap and Non Heap)

2. CPU Load Percentages (JVM process and System)

3. Garbage Collection Logs ( GC Type, Memory parameters before and after GC)

##Prerequisites

- [WSO2 DAS 3.1.0](http://wso2.com/products/data-analytics-server/) or later (Using lower version of DAS may required WSO2 ML and WSO2 CEP products)
- [WSO2 Anomaly Detection DAS Configuration Resources](https://github.com/wso2-incubator/automatic-anomaly-detection/tree/master/wso2-das)

##Installation

1. **JVM Monitor Agent** is built using Apache Maven. To build JMA, run:

        <JMA_HOME>/ mvn clean install

2. Configure DAS using [**WSO2 Anomaly Detection DAS Configuration Resources**](https://github.com/wso2-incubator/automatic-anomaly-detection/tree/master/wso2-das) 

##Deployment

1. [Run WSO2 DAS server](https://docs.wso2.com/display/DAS310/Running+the+Product)

2. Open the terminal and run:

