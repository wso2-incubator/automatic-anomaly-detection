## Introduction

This module is for processing Memory and Garbage collection statistics provided by jvm-monitor-agent using siddhi query. Mainly this has two features.

1. [Training-feature](https://github.com/wso2-incubator/automatic-anomaly-detection/tree/master/features/training-feature)
   - Collects JVM statistic data and process it to train a ML model.
2. [Prediction-feature](https://github.com/wso2-incubator/automatic-anomaly-detection/tree/master/features/prediction-feature)
   - Gives results in real-time using trained ML models and WSO2 Machine Learner Siddhi Extension.

## Prerequisites

1. [WSO2 DAS 3.1.0](http://wso2.com/products/data-analytics-server/) or later (Using lower version of DAS may required WSO2 ML and WSO2 CEP products)
2. Java Runtime Environment
3. Apache Maven

## Installation

### 1) Build features

   To build features, first navigate to ```<JMA_HOME>/features/``` Then run:
   
   ```
        mvn clean install
   ```
   **NOTE:** After doing this you can see a p2-repo folder inside ```<JMA_HOME>/features/target/```
      
### 2) Install features
   
  1. Extract WSO2 DAS binary distribution to your local file system.
  
  2. [Run WSO2 DAS server](https://docs.wso2.com/display/DAS310/Running+the+Product#RunningtheProduct-Startingtheserver)
  
  3. Log into WSO2 DAS Management Console
  
  4. Go to configurations in the WSO2 DAS server and click on the Features on the left navigation panel.   
  ![Feature Management](https://github.com/wso2-incubator/automatic-anomaly-detection/blob/master/docs/images/FeatureManagementScreenshots.png)
  
  5. Then click on the Add repository button.
  ![Repository Management](https://github.com/wso2-incubator/automatic-anomaly-detection/blob/master/docs/images/RepositoryManagementScreenshots.png)
  
  6. Give a name to the repository and enter your local p2-repo full path. (Eg:- ```<JMA_HOME>/features/target/p2-repo```)
  ![Add Repository](https://github.com/wso2-incubator/automatic-anomaly-detection/blob/master/docs/images/AddRepositoryScreenshots.png)
  
  7. Select the repository from Repository List, remove the tick from the Group features by category and click on the Find Features button.
  
  ![Find Features](https://github.com/wso2-incubator/automatic-anomaly-detection/blob/master/docs/images/FindFeaturesScreenshots.png)
  
  8. Select the one you want and click install. Then restart the server.
  ![Install Features](https://github.com/wso2-incubator/automatic-anomaly-detection/blob/master/docs/images/InstallFeaturesScreenshots.png)
  
 **Note:** Do not tick both.
