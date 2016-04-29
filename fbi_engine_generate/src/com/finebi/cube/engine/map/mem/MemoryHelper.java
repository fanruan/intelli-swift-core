package com.finebi.cube.engine.map.mem;

/**
 * Created by Connery on 2015/8/29.
 */
public class MemoryHelper {
    private Runtime runtime;

    public MemoryHelper() {
        this.runtime = Runtime.getRuntime();
    }

    public long leftMemory() {
        return leftMemoryByte() / (1024 * 1024);
    }

    public long leftMemoryByte() {
        return runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory());
    }

    public double leftMemoryPercent() {
        return (float) leftMemoryByte() / (float) runtime.maxMemory();
    }
}