#Iteration 2

##Model 1

1. Heap_used = heap used/heap max
2. Heap_used_growing =
        if (heap_used at t-1 s > heap_used at t )
        &nbsp;&nbsp;&nbsp;&nbsp;then 1
        else if ( heap_used at t-1 s < heap_used at t )
        &nbsp;&nbsp;&nbsp;&nbsp;then 0
        else 0.5

3. Heap_used_avg = average of heap_used in 5 sec
4. Heap_used_deviation = standard deviation of heap_used in 5 sec
5. Major_gc_freq = count of major gc happens in 1 sec
6. Eden_change = abs(eden_used_after - eden_used_before) / max(eden_used_after , eden_used_before)
7. Survivor_change = abs(survivor_used_after - survivor_used_before) / max(survivor_used_after, survivor_used_before)
8. Old_change = abs(old_used_after - old_used_before) / max(old_used_after,old_used_before)
9. Eden_growing =
        if (eden_used_after > eden_used_before )
        &nbsp;&nbsp;&nbsp;&nbsp;then 1
        else if ( eden_used_after <  eden_used_before)
        &nbsp;&nbsp;&nbsp;&nbsp;then 0
        else 0.5

10. Survivor_growing =
        if (survivor_used_after > survivor_used_before )
        &nbsp;&nbsp;&nbsp;&nbsp;then 1
        else if ( survivor_used_after <  survivor_used_before)
        &nbsp;&nbsp;&nbsp;&nbsp;then 0
        else 0.5

11. Old_growing =
        if (old_used_after > old_used_before )
        &nbsp;&nbsp;&nbsp;&nbsp;then 1
        else if ( old_used_after <  old_used_before)
            then 0
        else 0.5

12. GC_duration = average gc duration in 1 sec


##Model 2 (With Lagging features)

1. heap_used_1
2. heap_used_2,
3. heap_used_3,
4. heap_allocated_1,
5. heap_allocated_2,
6. heap_allocated_3,
7. heap_used_change_1,
8. heap_used_change_2,
9. heap_used_change_3,
10. heap_allocated_change_1 ,
11. heap_allocated_change_2 ,
12. heap_allocated_change_3,
13. heap_used_growing_1 ,
13. heap_used_growing_2,
14. heap_used_growing_3,
15. heap_allocated_growing_1 ,
16. heap_allocated_growing_2,
17. heap_allocated_growing_3,
18. major_gc_freq_1,
19. major_gc_freq_2,
20. major_gc_freq_3,
21. major_gc_happend_1,
22. major_gc_happend_2,
23. major_gc_happend_3,
24. gc_duration_1 ,
25. gc_duration_2 ,
26. gc_duration_3,
27. eden_change,
28. survivor_change,
29. old_change,
30. eden_growing,
31. survivor_growing,
32. old_growing





