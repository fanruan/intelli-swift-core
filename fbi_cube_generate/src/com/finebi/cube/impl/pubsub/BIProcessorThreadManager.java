package com.finebi.cube.impl.pubsub;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class created on 2016/4/13.
 *
 * @author Connery
 * @since 4.0
 */
public class BIProcessorThreadManager {
    private ExecutorService executorService;
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
        executorService = Executors.newFixedThreadPool(4);
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
