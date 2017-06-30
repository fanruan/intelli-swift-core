package com.fr.bi.cal.analyze.cal.multithread;

import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.manager.PerformancePlugManager;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Hiram on 2015/5/14.
 */
public class MultiThreadManagerImpl {
    //同时进行的多线程计算数量，
    private AtomicInteger count;
    private static MultiThreadManagerImpl ourInstance = new MultiThreadManagerImpl();
    private static BIMultiThreadExecutor executor = new BIMultiThreadExecutor();

    private MultiThreadManagerImpl() {
        count = new AtomicInteger(0);
    }

    private boolean isMultiCall() {
        return PerformancePlugManager.getInstance().isUseMultiThreadCal();
    }

    public static MultiThreadManagerImpl getInstance() {
        return ourInstance;
    }



    public BIMultiThreadExecutor getExecutorService() {
        //小于3个才进行多线程计算，并发高的时候多线程反而是累赘
        if (isMultiCall() && count.get() < 3) {
            count.incrementAndGet();
            return executor;
        }
        return null;
    }



    public void releaseCurrentThread(BISession session, BIMultiThreadExecutor executor) {
        if (executor != null){
            executor.releaseCurrentThread(session);
            count.decrementAndGet();
        }
    }

}