#Iteration 2

##Model

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







