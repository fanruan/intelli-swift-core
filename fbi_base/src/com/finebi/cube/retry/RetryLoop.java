package com.finebi.cube.retry;

import org.apache.zookeeper.KeeperException;

import java.util.concurrent.Callable;

/**
 * Created by Sean on 2015/5/7.
 * implement the retry loop
 */
public class RetryLoop {
    private boolean isDone;
    private RetryPolicy retryPolicy;
    private long startTime;
    private int retryCount;
    private Sleeper sleeper = new Sleeper();

    public RetryLoop() {
        reset();
    }

    public void initial(RetryPolicy policy, RetryLoop retryLoop) {
        this.retryPolicy = policy;
    }


    public boolean shouldRetry() {
        return !isDone;
    }


    public void markComplete() {
        isDone = true;
    }

    public void reset() {
        retryCount = 0;
        startTime = System.currentTimeMillis();
    }

    public void takeException(Exception exception) throws Exception {
        boolean throwException = false;
        if (exception instanceof KeeperException.ConnectionLossException) {
        }

        if (retryPolicy.allowRetry(retryCount++, System.currentTimeMillis() - startTime, sleeper)) {
            throwException = false;
        } else {
            throwException = true;
        }
        if (throwException) {
            throw exception;
        }
    }

    public static <T> T retry(Callable<T> proc, RetryLoop retryLoop) throws Exception {
        T result = null;
        retryLoop.reset();
        while (retryLoop.shouldRetry()) {
            try {
                result = proc.call();
                retryLoop.markComplete();
            } catch (Exception ex) {
                ex.printStackTrace();
                retryLoop.takeException(ex);
            }
        }
        return result;

    }
}
