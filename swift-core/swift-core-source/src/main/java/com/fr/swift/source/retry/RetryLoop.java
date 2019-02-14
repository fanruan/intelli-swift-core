package com.fr.swift.source.retry;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;

import java.util.concurrent.Callable;

/**
 * Created by Connery on 2015/5/7.
 * 重试当前
 */
public class RetryLoop {
    private boolean isDone;
    private RetryPolicy retryPolicy;
    private long startTime;
    private int retryCount;
    private Sleeper sleeper = new Sleeper();
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RetryNTimes.class);

    public RetryLoop() {
        reset();
    }


    public static <T> T retry(Callable<T> proc, RetryLoop retryLoop) throws Exception {
        T result = null;
        retryLoop.reset();
        while (retryLoop.shouldRetry()) {
            try {
                result = proc.call();
                retryLoop.markComplete();
            } catch (Exception ex) {
                retryLoop.takeException(ex);
                LOGGER.info("retry times: " + retryLoop.retryCount);
            }
        }
        return result;

    }
    public void initial(RetryPolicy policy) {
        this.retryPolicy = policy;

    }

    /**
     * 判断是否继续执行
     *
     * @return
     */
    public boolean shouldRetry() {
        return !isDone;
    }

    /**
     * 强制结束当前重试
     */
    public void markComplete() {
        isDone = true;
    }

    public void reset() {
        retryCount = 0;
        isDone = false;
        startTime = System.currentTimeMillis();
    }

    /**
     * 处理异常
     *
     * @param exception
     * @throws Exception
     */
    public void takeException(Exception exception) throws Exception {
        if (retryPolicy.allowRetry(retryCount++, System.currentTimeMillis() - startTime, sleeper)) {
            SwiftLoggers.getLogger(RetryLoop.class).error(exception.getMessage(), exception);
        } else {
            throw exception;
        }
    }
}