#Generate OOM 

Sample application to generate Out of memory error. Continuously creates arrays growing the size of arrays.

##Compile

        javac GenerateOOM.java

##Run

        java GenerateOOM
or
        java GenerateOOM <multiplier>
or
        java GenerateOOM <multiplier> <addition>
or
        java GenerateOOM <multiplier> <addition> <limit>

|***multiplier***| multiply the array size. ```new array size = old array size * multiplier``` (default = 1)|
