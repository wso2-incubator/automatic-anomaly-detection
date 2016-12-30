#WSO2 Anomaly Detection DAS Configuration Resources

##Introduction

This package contains the resources that are required to setup [WSO2 Data Analytic Server(DAS)](http://wso2.com/products/data-analytics-server/) to collect JVM monitoring data from [JVM Monitoring Agent](https://github.com/wso2-incubator/automatic-anomaly-detection/tree/master/jvm-monitor-agent) in order to train [WSO2 ML model(ML)](http://wso2.com/products/machine-learner/). Moreover, it contains models created using [sample test applications](https://github.com/wso2-incubator/automatic-anomaly-detection/tree/master/jvm-monitor-agent/src/samples/applications).


##Prerequisites
- [WSO2 DAS 3.1.0](http://wso2.com/products/data-analytics-server/) or later (Using lower version of DAS may required WSO2 ML and WSO2 CEP products)
- [WSO2 JVM Monitor Agent](https://github.com/wso2-incubator/automatic-anomaly-detection/tree/master/jvm-monitor-agent)

##How to Use
###Configure DAS and Training Models
1. Copy all the files in [**wso2-das/training**](https://github.com/wso2-incubator/automatic-anomaly-detection/tree/master/wso2-das/training) directory to ```<DAS_HOME>/repository/deployment/server/<relevant_directory>``` (**DO NOT** replace your existing files)

2. [Run WSO2 DAS server](https://docs.wso2.com/display/DAS310/Running+the+Product)

3. [Run JVM Monitoring Agent](https://github.com/wso2-incubator/automatic-anomaly-detection/tree/master/jvm-monitor-agent)

4. After collecting JVM usage data for  sufficient time to train the model, [create a model](https://docs.wso2.com/display/ML120/Generating+Models) using **anomaly detection with unlabeled data** algorithm in WSO2 ML (use [Machine Learner Wizard](https://docs.wso2.com/display/DAS310/Predicitve+Analytics) to run WSO2 ML)

###Anomaly Detection Using Real-time Data

1. Copy all the files in [**wso2-das/detection**](https://github.com/wso2-incubator/automatic-anomaly-detection/tree/master/wso2-das/detection) directory to ```<DAS_HOME>/repository/deployment/server/<relevant_directory>``` except [models](https://github.com/wso2-incubator/automatic-anomaly-detection/tree/master/wso2-das/detection/models) directory (**DO NOT** replace existing files)

2. Copy sample models in [**models**](https://github.com/wso2-incubator/automatic-anomaly-detection/tree/master/wso2-das/detection/models) directory or the generated models to  ```<DAS_HOME>/models``` (**DO NOT** replace existing files)

3. [Run WSO2 DAS server](https://docs.wso2.com/display/DAS310/Running+the+Product)

4. Go to Execution Plans in main tab and select **'UsagePredictionExecutionPlan'** execution plan in edit mode

5. Replace the ML predict() function model path with the generated model path

        from PredictionDataStream#ml:predict('<DAS_HOME>/models/<Model_Name>' , 'string', 99.0, ....

6. [Run JVM Monitoring Agent](https://github.com/wso2-incubator/automatic-anomaly-detection/tree/master/jvm-monitor-agent)

You can use the existing event publisher to observe the loggers generated to display prediction results.

   
    
    
