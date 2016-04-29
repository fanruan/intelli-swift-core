package com.fr.bi.cluster.retry;

import java.util.concurrent.TimeUnit;

/**
 * Created by FineSoft on 2015/5/7.
 * 重试N次
 */
public class RetryNTimes implements RetryPolicy {

    private int maxTimes;
    private long maxSleepTime;

    public RetryNTimes(int maxTimes, long maxSleepTime) {
        this.maxTimes = maxTimes;
        this.maxSleepTime = maxSleepTime;
    }

    @Override
    public boolean allowRetry(int currentCount, long elapsedTimeMs, Sleeper sleeper) {
        if (currentCount < maxTimes) {
//            System.out.println("等待："+getSleepTime(currentCount, elapsedTimeMs)+"秒");
            sleeper.sleep(getSleepTime(currentCount, elapsedTimeMs), TimeUnit.MILLISECONDS);
//            System.out.println("等待结束");
            return true;
        }
        return false;
    }

    protected long getSleepTime(int currentCount, long elapsedTimeMs) {
        return Math.min(maxSleepTime, currentCount * elapsedTimeMs);
    }
}