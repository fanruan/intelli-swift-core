package com.fr.swift.retry;

/**
 * Created by FineSoft on 2015/5/7.
 */
public interface RetryPolicy {
    boolean allowRetry(int currentCount, long elapsedTimeMs, Sleeper sleeper);
}