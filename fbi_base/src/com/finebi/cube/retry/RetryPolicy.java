package com.finebi.cube.retry;

/**
 * Created by Sean on 2015/5/7.
 */
public interface RetryPolicy {
    boolean allowRetry(int currentCount, long elapsedTimeMs, Sleeper sleeper);
}
