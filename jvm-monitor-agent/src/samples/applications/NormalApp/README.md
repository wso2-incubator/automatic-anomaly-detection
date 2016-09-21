#Normal App

Sample application to generate Out of memory error. 

##Compile

        javac NormalApp.java

##Run

        java NormalApp
or

        java NormalApp <int> numbersOfThreads <int> threadPoolSize
or

        java NormalApp <int> numbersOfThreads <int> threadPoolSize <int> maxMainListSize <int>  arraySize <int> threadsSleepTime

        Numbers of Threads, Thread Pool size (eg:- 3 5)
             * 2). Numbers of Threads, Thread Pool size, Max main list size, Array size, Threads sleep time (eg:- 3 5 100000 10 1500)



| Options  |  Description  | Default |
| --------|---------|-------|
|***\<int> numbersOfThreads*** | Numbers of Threads | 3 |
| ***\<int> threadPoolSize*** | Thread Pool size, | 5 |
| ***\<int> maxMainListSize*** | Max main list size | 100000 |
| ***\<int> arraySize*** | Array size | 10 |
| ***\<int> threadsSleepTime*** | Threads sleep time | 1500 |
