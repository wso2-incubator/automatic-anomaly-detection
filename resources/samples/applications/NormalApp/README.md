#Normal App

Sample application to simulate normal conditions of application.

##Compile

        javac NormalApp.java

##Run

        java NormalApp
or

        java NormalApp <int> numbersOfThreads <int> threadPoolSize
or

        java NormalApp <int> numbersOfThreads <int> threadPoolSize <int> maxMainListSize <int>  arraySize <int> threadsSleepTime



| Options  |  Description  | Default |
| --------|---------|-------|
|***\<int> numbersOfThreads*** | Numbers of Threads | 3 |
| ***\<int> threadPoolSize*** | Thread Pool size, | 5 |
| ***\<int> maxMainListSize*** | Max main list size | 100000 |
| ***\<int> arraySize*** | Array size | 10 |
| ***\<int> threadsSleepTime*** | Threads sleep time | 100 |

---

**CPU and Memory behaviour of this App**

![Normal App 1]
(https://github.com/wso2-incubator/automatic-anomaly-detection/blob/master/jvm-monitor-agent/src/samples/applications/NormalApp/NormalApp.jpg)*Draw a graph using [VisualVM](https://visualvm.java.net)*
