package com.fr.swift.setting;


public class PerformancePlugManager implements PerformancePlugManagerInterface {
    private boolean isDirectGenerating;
    private int retryMaxTimes = 3;
    private long retryMaxSleepTime = 100L;
    private int reIndexRowCount = 10000000;

    private boolean diskSort = true;
    private long diskSortDumpThreshold = 1 << 15;

    private boolean useNumberType;
    private double minCubeFreeHDSpaceRate = 2;
    private int maxNioFileSize;

    @Override
    public boolean isDirectGenerating() {
        return isDirectGenerating;
    }

    public int getRetryMaxTimes() {
        return retryMaxTimes;
    }

    public void setRetryMaxTimes(int retryMaxTimes) {
        this.retryMaxTimes = retryMaxTimes;
    }

    public long getRetryMaxSleepTime() {
        return retryMaxSleepTime;
    }

    public void setRetryMaxSleepTime(long retryMaxSleepTime) {
        this.retryMaxSleepTime = retryMaxSleepTime;
    }

    public int getReIndexRowCount() {
        return reIndexRowCount;
    }

    public boolean isUseNumberType() {
        return useNumberType;
    }

    @Override
    public boolean isDiskSort() {
        return diskSort;
    }

    @Override
    public long getDiskSortDumpThreshold() {
        return diskSortDumpThreshold;
    }

    private static final PerformancePlugManager INSTANCE = new PerformancePlugManager();

    private PerformancePlugManager() {
    }

    public static PerformancePlugManager getInstance() {
        return INSTANCE;
    }

    public double getMinCubeFreeHDSpaceRate() {
        return minCubeFreeHDSpaceRate;
    }

    public int getMaxNioFileSize() {
        return maxNioFileSize;
    }
}