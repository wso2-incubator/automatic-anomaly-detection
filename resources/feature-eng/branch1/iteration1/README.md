#Iteration 1

##Basic Set of features

###Memory Usage

1. Heap allocated percentage relative to heap max - heap_allocated
			= ( Heap allocated / Heap max )
2. Heap used memory percentage relative to heap allocated  - heap_used
= ( Heap used / Heap allocation )


###GC Logs

1. Minor GC frequency - minor_gc_freq = no of minor GCs / time ( count of minor GCs in one second

2. Major GC frequency - major_gc_freq = no of minor GCs / time ( count of minor GCs in one second

3. GC duration - gc_duration = avg (gc_duration) of one second

4. Percentage of Eden Space memory cleared relative to Memory Before GC - eden_cleared = (eden_used_before - eden_used_after)/eden_used_before

5. Percentage of Survivor Space memory cleared relative to Memory Before GC - survivor_cleared = (survivor_used_before - survivor_used_after)/survivor_used_before

6. Percentage of Old Generation Space memory cleared relative to Memory Before GC - old_cleared = (old_used_before - old_used_after)/old_used_before

7. Percentage of Eden Used Space memory after GC relative to committed Memory after GC - eden_used = eden_used_after/eden_committed_after

8. Percentage of Survivor used Space memory after GC  relative to committed Memory after GC - survivor_used = survivor_used_after/survivor_committed_after

9. Percentage of Old Generation used Space memory after GC relative to committed Memory after GC - old_used = old_used_after/old_committed_after

10. Percentage of Eden Space  committed Memory after GC relative to max Memory after GC - eden_committed = eden_committed_after/eden_max_after

11. Percentage of Survivor Space committed Memory after GC relative to max Memory after GC - survivor_committed = survivor_committed_after/ survivor_max_after

12. Percentage of Old Generation Space  committed Memory after GC relative to max Memory after GC - old_committed = old_committed_after/old_max_after

###CPU stream

1. Process CPU load percentage - process_load

2. System CPU load percentage - system_load


##Model 1 - With lagging features and minor gc frequency  (at time t)

###Memory Usage

1. Heap_allocated_1  - heap_ allocated at time t-2 s
2. Heap_allocated_2 - heap_ allocated at time t-1 s
3. Heap_allocated_3 - heap_allocated at time t s
4. Heap_used_1 - heap_used at time t-2 s
5. Heap_used_2 - heap_used at time t-1 s
6. Heap_used_3 - heap_used at time t s

###CPU Usage

7. Process_cpu_load_1  - process_cpu_load at time t-2 s
8. Process_cpu_load_2  - process_cpu_load at time t-1 s
9. Process_cpu_load_3  - process_cpu_load at time t s
10. System_cpu_load_1 - system_cpu_load at time t-2 s
11. System_cpu_load_2 - system_cpu_load at time t-1 s
12. System_cpu_load_3 - system_cpu_load at time t s

###GC Log

1. Minor_gc_freq_1 - Minor_gc_freq in t-2 s
2. Minor_gc_freq_2 - Minor_gc_freq in t-1 s
3. Minor_gc_freq_3 - Minor_gc_freq in t s
4. Major_gc_freq_1 - Major_gc_freq in t-2 s
5. Major_gc_freq_2 - Major_gc_freq in t-1 s
6. Major_gc_freq_3 - Major_gc_freq in t s
7. GC_duration_1 - Average GC duration in t-2 s
8. GC_duration_2 - Average GC duration in t-1 s
9. GC_duration_3 - Average GC duration in t s
10. Eden_Cleared
11. Eden_growing
12. Survivor_cleared
13. Survivor_growing
14. Old_cleared
15. Old_growing
16. Eden_used
17. Survivor_used
18. Old_used
19. Eden_Committed
20. Survivor_committed
21. Old_committed


##Model 2 - With lagging features and no minor gc data (at time t)

###Memory Usage

1. Heap_allocated_1  - heap_ allocated at time t-2 s
2. Heap_allocated_2 - heap_ allocated at time t-1 s
3. Heap_allocated_3 - heap_allocated at time t s
4. Heap_used_1 - heap_used at time t-2 s
5. Heap_used_2 - heap_used at time t-1 s
6. Heap_used_3 - heap_used at time t s

###CPU Usage

7. Process_cpu_load_1  - process_cpu_load at time t-2 s
8. Process_cpu_load_2  - process_cpu_load at time t-1 s
9. Process_cpu_load_3  - process_cpu_load at time t s
10. System_cpu_load_1 - system_cpu_load at time t-2 s
11. System_cpu_load_2 - system_cpu_load at time t-1 s
12. System_cpu_load_3 - system_cpu_load at time t s

###GC Log ( Only for major gc log events)

13. Minor_gc_freq_1 - Minor_gc_freq in t-2 s
14. Minor_gc_freq_2 - Minor_gc_freq in t-1 s
15. Minor_gc_freq_3 - Minor_gc_freq in t s
16. Major_gc_freq_1 - Major_gc_freq in t-2 s
17. Major_gc_freq_2 - Major_gc_freq in t-1 s
18. Major_gc_freq_3 - Major_gc_freq in t s
19. GC_duration_1 - Average GC duration in t-2 s
20. GC_duration_2 - Average GC duration in t-1 s
21. GC_duration_3 - Average GC duration in t s
22. Eden_Cleared
23. Eden_growing
24. Survivor_cleared
25. Survivor_growing
26. Old_cleared
27. Old_growing
28. Eden_used
29. Survivor_used
30. Old_used
31. Eden_Committed
32. Survivor_committed
33. Old_committed
