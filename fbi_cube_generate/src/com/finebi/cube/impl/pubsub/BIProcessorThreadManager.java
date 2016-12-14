package com.finebi.cube.impl.pubsub;

import com.fr.bi.manager.PerformancePlugManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * This class created on 2016/4/13.
 *
 * @author Connery
 * @since 4.0
 */
public class BIProcessorThreadManager {
    protected ExecutorService executorService;
    protected static BIProcessorThreadManager instance;

    public static BIProcessorThreadManager getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (BIProcessorThreadManager.class) {
            if (instance == null) {
                instance = new BIProcessorThreadManager();
            }
            return instance;
        }
    }

    protected BIProcessorThreadManager() {
        executorService = Executors.newFixedThreadPool(PerformancePlugManager.getInstance().getBiThreadPoolSize());
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public boolean isLeisure() {
        return ((ThreadPoolExecutor) executorService).getActiveCount() == 0;
    }
}
