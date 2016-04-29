package com.fr.bi.cal.analyze.cal.Executor;


import com.fr.bi.cal.analyze.exception.IllegalThresholdException;

/**
 * Created by Connery on 2014/12/14.
 */
public abstract class LazyExecutorPartner<T> {
    protected LazyExecutor lazyExecutor;

    public void startExecutor() {
        lazyExecutor.start();
    }

    protected void waitExecutor(long count) {
        long old = count;
//        count = Integer.MAX_VALUE;
        if (shouldWait(count)) {
            Object listener = new Object();
            synchronized (lazyExecutor) {
                try {
                    if (lazyExecutor.isThresholdLegal(count)) {
                        lazyExecutor.setThreshold(count);
                    }
                    lazyExecutor.registerThreshold(count, listener);
                    lazyExecutor.turnOnExecutor();
                } catch (IllegalThresholdException ex) {
                    return;
                }
            }
            synchronized (listener) {
                try {
//                    lazyExecutor.lockCount();
                    if (shouldWait(count)) {
                        listener.wait();
                    }
                } catch (InterruptedException e) {
                } finally {
//                    lazyExecutor.unlockCount();
                }
            }
        }
    }

    private boolean shouldWait(long count) {
        return count > lazyExecutor.getCurrentCount() && !lazyExecutor.isFinished();
    }

    public abstract T getCorrespondingContent(long count);

}