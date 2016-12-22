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
    private ExecutorService executorService;
    private ExecutorService transportExecutorService;
    private static BIProcessorThreadManager instance;

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

    private BIProcessorThreadManager() {
        executorService = Executors.newFixedThreadPool(PerformancePlugManager.getInstance().getBiThreadPoolSize());
        transportExecutorService = Executors.newFixedThreadPool(PerformancePlugManager.getInstance().getBiTransportThreadPoolSize());
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public ExecutorService getTransportExecutorService() {
        return transportExecutorService;
    }

    public boolean isLeisure() {
        return ((ThreadPoolExecutor) executorService).getActiveCount() == 0 && ((ThreadPoolExecutor) transportExecutorService).getActiveCount() == 0;
    }
}
