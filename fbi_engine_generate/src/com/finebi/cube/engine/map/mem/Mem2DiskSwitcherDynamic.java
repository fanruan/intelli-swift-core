package com.finebi.cube.engine.map.mem;

import com.finebi.cube.engine.map.ExternalMap;

import java.util.TreeMap;

/**
 * Created by FineSoft on 2015/7/17.
 */
public class Mem2DiskSwitcherDynamic<K, V> extends Mem2DiskSwitcher<K, V> {
    private int thresholdMemInMillion;
    private Runtime runtime;

    @Override
    long getThreshold() {
        if (leftMemory() > thresholdMemInMillion) {
            return Long.MAX_VALUE;
        } else {
            return -1;
        }
    }

    private long leftMemory() {
        long leftByte = runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory());
        return leftByte / (1024 * 1024);
    }

    public Mem2DiskSwitcherDynamic(TreeMap mem, ExternalMap disk, int thresholdMemInMillion) {
        super(mem, disk);
        this.runtime = Runtime.getRuntime();
        this.thresholdMemInMillion = thresholdMemInMillion;
    }
}