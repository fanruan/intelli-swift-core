package com.fr.swift.source.retry;

/**
 * Created by FineSoft on 2015/5/7.
 */
public interface RetryPolicy {
    boolean allowRetry(int currentCount, long elapsedTimeMs, Sleeper sleeper);
}