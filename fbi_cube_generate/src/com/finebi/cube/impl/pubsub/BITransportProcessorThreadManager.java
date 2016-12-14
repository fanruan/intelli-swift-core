package com.finebi.cube.impl.pubsub;

import com.fr.bi.manager.PerformancePlugManager;

import java.util.concurrent.Executors;

/**
 * Created by roy on 2016/12/14.
 */
public class BITransportProcessorThreadManager extends BIProcessorThreadManager {

    private BITransportProcessorThreadManager() {
        executorService = Executors.newFixedThreadPool(PerformancePlugManager.getInstance().getBiTransportThreadPoolSize());
    }
}
