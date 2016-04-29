package com.fr.bi.cal.analyze.cal.Executor;


import com.fr.bi.cal.analyze.exception.IllegalThresholdException;
import com.fr.bi.cal.analyze.exception.TerminateExecutorException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Connery on 2014/12/14.
 * 主要用途是遍历属性iterator，到达当前this.threshold后，等待。
 * 当有新的threshold时，换新并且继续遍历。
 */
public class LazyExecutor<T, F> extends Thread {

    private static final int PRE_EVALUATE = 0;
    Map<Long, ArrayList<Object>> thresholdListeners;
    private long threshold;
    private boolean isFinished;
    private long currentCount;
    private ILazyExecutorOperation<T, F> lazyExecutorOperation;
    private Iterator<T> iterator;
    private boolean executorStatus_On;
    private volatile Thread stop_flag;
    private volatile boolean interrupt_flag;
    private ReentrantLock lock = new ReentrantLock();
    private Condition exector = lock.newCondition();
    private Condition count = lock.newCondition();

    public LazyExecutor() {
        this.threshold = -2;
        this.isFinished = false;
        this.executorStatus_On = false;
        /**
         * 为了保证currentCount获取到的数值对应数据全部生成完成。
         * 将currentCount移后一位，从-1开始。
         * 第一个数据生成好后，currentCount=0
         * 第二个数据生成好后，currentCount=1
         * 等等，继续进行
         */
        this.currentCount = -1;
        thresholdListeners = new ConcurrentHashMap<Long, ArrayList<Object>>();

    }

    public void initial(ILazyExecutorOperation lazyExecutorOperation, Iterator<T> iterator) {
        this.lazyExecutorOperation = lazyExecutorOperation;
        this.iterator = iterator;
    }

    public long getCurrentCount() {
        return currentCount;
    }

    private void increaseCurrentCount() {
        try {
            lockCount();
            currentCount++;
        } finally {
            unlockCount();
        }
    }

    public long getThreshold() {
        return threshold;
    }

    public void setThreshold(long threshold) throws IllegalThresholdException {
        if (isThresholdLegal(threshold)) {
            this.threshold = threshold + PRE_EVALUATE;
        } else {
            throw new IllegalThresholdException();
        }
    }

    public void turnOnExecutor() {
        synchronized (this) {
            if (!executorStatus_On) {
                executorStatus_On = true;
                this.notifyAll();
            }
        }
    }

    public void nofity() {
        exector.notifyAll();
    }

    public boolean isThresholdLegal(long threshold) {
        return threshold >= this.threshold;
    }

    public void registerThreshold(long threshold, Object listener) {
        synchronized (thresholdListeners) {
            if (thresholdListeners.containsKey(threshold)) {
                thresholdListeners.get(threshold).add(listener);
            } else {
                ArrayList<Object> listeners = new ArrayList<Object>(1);
                listeners.add(listener);
                thresholdListeners.put(threshold, listeners);
            }
        }
        checkThresholdListener();
    }

    public boolean isFinished() {
        return isFinished;
    }

    private void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public void finishJob() {
        setFinished(true);
    }

    /**
     * 线程run方法
     */
    @Override
    public void run() {
        createRootBaseNodes();
    }

    private void checkThresholdListener() {
        long count = getCurrentCount();
        Set<Long> thresholds = thresholdListeners.keySet();
        Iterator<Long> it = thresholds.iterator();
        while (it.hasNext()) {
            long tempCount = it.next();
            if (tempCount <= count) {
                notifyThresholdListener(tempCount);
            }
        }
    }

    private void notifyThresholdListener(long count) {
        synchronized (thresholdListeners) {
            if (thresholdListeners.containsKey(count)) {
                ArrayList<Object> listeners = thresholdListeners.get(count);
                thresholdListeners.remove(count);
                Iterator<Object> it = listeners.iterator();
                while (it.hasNext()) {
                    Object listener = it.next();
                    synchronized (listener) {
                        listener.notify();
                    }
                }
            }
        }
    }

    private void createRootBaseNodes() {
        try {
            stop_flag = Thread.currentThread();
            interrupt_flag = false;
            lazyExecutorOperation.initPrecondition();
            while (iterator.hasNext() && !isInterrupted() && !currentThreadKilled()) {
                while (shouldExecutor()) {
                    T itNext = iterator.next();
                    F preCondition = lazyExecutorOperation.mainTaskConditions(itNext);
                    if (lazyExecutorOperation.jumpCurrentOne(preCondition)) {
                        continue;
                    } else {
                        lazyExecutorOperation.mainTask(itNext, preCondition);
                        increaseCurrentCount();
                        checkThresholdListener();
                    }
                }
                if (iterator.hasNext()) {
                    synchronized (this) {
                        if (!shouldExecutor()) {
                            try {
                                this.executorStatus_On = false;
                                this.wait();
                            } catch (InterruptedException e) {
                                interruptCurrentThread();
                            }
                        }
                    }
                }
            }
            if (!currentThreadKilled()) {
                lazyExecutorOperation.endCheck();
            }

        } catch (TerminateExecutorException ex) {
            lazyExecutorOperation.executorTerminated();
        } finally {
            finishJob();
            notifyRestListeners();
        }
    }

    private boolean shouldExecutor() {
        return iterator.hasNext() && currentCount <= getThreshold() && !currentThreadKilled();
    }

    public void stopRunning() {
        stop_flag = null;
    }

    public boolean currentThreadKilled() {
        return stop_flag == null || interrupt_flag;
    }

    private void interruptCurrentThread() {
        this.interrupt_flag = true;
    }

    private void notifyRestListeners() {
        synchronized (thresholdListeners) {
            Iterator<ArrayList<Object>> itValues = thresholdListeners.values().iterator();
            while (itValues.hasNext()) {
                Iterator<Object> itListener = itValues.next().iterator();
                while (itListener.hasNext()) {
                    Object listener = itListener.next();
                    synchronized (listener) {
                        listener.notify();
                    }
                }
            }
        }
    }

    public void lockCount() {
        lock.lock();
    }

    public void unlockCount() {
        lock.unlock();
    }
}