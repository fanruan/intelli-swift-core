package com.fr.swift.source.retry;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;

import java.util.concurrent.TimeUnit;

/**
 * Created by FineSoft on 2015/5/7.
 * 重试N次
 */
public class RetryNTimes implements RetryPolicy {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RetryNTimes.class);

    private int maxTimes;
    private long maxSleepTime;

    public RetryNTimes(int maxTimes, long maxSleepTime) {
        this.maxTimes = maxTimes;
        this.maxSleepTime = maxSleepTime;
    }

    @Override
    public boolean allowRetry(int currentCount, long elapsedTimeMs, Sleeper sleeper) {
        if (currentCount < maxTimes) {
            LOGGER.info("wait " + getSleepTime(currentCount, elapsedTimeMs) + " millis seconds");
            sleeper.sleep(getSleepTime(currentCount, elapsedTimeMs), TimeUnit.MILLISECONDS);
            LOGGER.info("wait over");
            return true;
        }
        return false;
    }

    protected long getSleepTime(int currentCount, long elapsedTimeMs) {
        return Math.min(maxSleepTime, currentCount * elapsedTimeMs);
    }
}