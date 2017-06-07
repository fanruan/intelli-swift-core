package com.fr.bi.stable.structure.queue;

import com.finebi.cube.common.log.BILoggerFactory;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Lucifer on 2017-5-22.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class CubeTaskCondition {

    private Lock lock;
    private Condition condition;

    private static CubeTaskCondition instance;

    static {
        instance = new CubeTaskCondition();
    }

    private CubeTaskCondition() {
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public static CubeTaskCondition getInstance() {
        return instance;
    }

    public void await() {
        lock.lock();
        try {
            condition.await();
        } catch (InterruptedException e) {
            BILoggerFactory.getLogger(CubeTaskCondition.class).error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    public void signal() {
        lock.lock();
        try {
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public void signalAll() {
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}