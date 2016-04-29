package com.fr.bi.cluster.retry;

import com.fr.bi.cluster.wrapper.ZooKeeperWrapper;
import com.fr.bi.stable.utils.code.BILogger;
import org.apache.zookeeper.KeeperException;

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
    private ZooKeeperWrapper wrapper;

    public RetryLoop() {
        reset();
    }

    public static <T> T retry(Callable<T> proc, ZooKeeperWrapper zookeeperWrapper) throws Exception {
        RetryLoop retryLoop = zookeeperWrapper.getRetryLoop();
        T result = null;
        retryLoop.reset();
        while (retryLoop.shouldRetry()) {
            try {
                result = proc.call();
                retryLoop.markComplete();
            } catch (Exception ex) {
                 BILogger.getLogger().error(ex.getMessage(), ex);
                retryLoop.takeException(ex);
            }
        }
        return result;

    }

    public void initial(RetryPolicy policy, ZooKeeperWrapper wrapper) {
        this.retryPolicy = policy;
        this.wrapper = wrapper;
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
        startTime = System.currentTimeMillis();
    }

    /**
     * 处理异常
     *
     * @param exception
     * @throws Exception
     */
    public void takeException(Exception exception) throws Exception {
        boolean throwException = false;
        if (exception instanceof KeeperException.ConnectionLossException) {
//            wrapper.getZookeeperHandler().reconnect();
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
}