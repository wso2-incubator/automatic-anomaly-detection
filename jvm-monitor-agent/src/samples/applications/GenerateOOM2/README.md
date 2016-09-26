#Generate OOM 2

Sample application to generate Out of memory error. 

##Compile

        javac GenerateOOM2.java

##Run

        java GenerateOOM2
or

        java GenerateOOM2 <int> sleepTime
or

        java GenerateOOM2 <int> sleepTime <int> numberOfThread



| Options  |  Description  | Default |
| --------|---------|-------|
|***\<int> sleepTime*** | Thread sleep time (ms) | 0 |
| ***\<int> numberOfThread*** | Number of Threads | 2 |

---

**CPU and Memory behaviour of this App**

![Generate OOM 2]
(https://github.com/wso2-incubator/automatic-anomaly-detection/blob/master/jvm-monitor-agent/src/samples/applications/GenerateOOM2/GenerateOOM2.jpg)*Draw a graph using [VisualVM](https://visualvm.java.net)*
