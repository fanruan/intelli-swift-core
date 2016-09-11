package com.finebi.cube.retry;

import java.util.concurrent.TimeUnit;

/**
 * Created by Sean on 2015/5/7.
 * retry N time
 */
public class RetryNTimes implements RetryPolicy {

    private int maxTimes;
    private long maxSleepTime;

    public RetryNTimes(int maxTimes, long maxSleepTime) {
        this.maxTimes = maxTimes;
        this.maxSleepTime = maxSleepTime;
    }

    public boolean allowRetry(int currentCount, long elapsedTimeMs, Sleeper sleeper) {
        if (currentCount < maxTimes) {
            System.out.println(getSleepTime(currentCount, elapsedTimeMs) + "??");
            sleeper.sleep(getSleepTime(currentCount, elapsedTimeMs), TimeUnit.MILLISECONDS);
            return true;
        }
        return false;
    }

    protected long getSleepTime(int currentCount, long elapsedTimeMs) {
        return Math.min(maxSleepTime, currentCount * elapsedTimeMs);
    }
}
