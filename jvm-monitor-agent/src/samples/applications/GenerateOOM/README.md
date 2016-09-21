#Generate OOM 

Sample application to generate Out of memory error. Continuously creates arrays growing the size of arrays.

##Compile

        javac GenerateOOM.java

##Run

        java GenerateOOM
or

        java GenerateOOM <double> multiplier
or

        java GenerateOOM <double> multiplier <int> addition
or

        java GenerateOOM <double> multiplier <int> addition <long> limit


| Options  |  Description  | Default |
| --------|---------|-------|
|***\<double> multiplier*** | Multiply the array size. ```new_array_size = old_array_size * multiplier``` | 1 |
| ***\<int> addition*** | Increase the array type by addition value ```new_array_size = old_array_size + addition``` | 10000 |
| ***\<long> limit*** | Recomend to perform a GC after number of arrays exceeding the limit | 10000000 |

***Changing the options can control the time before OOM***
