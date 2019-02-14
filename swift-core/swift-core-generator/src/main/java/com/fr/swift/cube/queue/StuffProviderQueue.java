package com.fr.swift.cube.queue;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class created on 2018-1-16 16:00:10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class StuffProviderQueue {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(StuffProviderQueue.class);

    private static int MAX_CAPACITY = 10000;

    private BlockingQueue<ImportStuff> providerQueue = new LinkedBlockingQueue<ImportStuff>(MAX_CAPACITY);

    public static StuffProviderQueue getQueue() {
        return SingletonHolder.INSTANCE;
    }

    public void put(ImportStuff provider) throws InterruptedException {
        providerQueue.put(provider);
    }

    public ImportStuff poll() {
        return providerQueue.poll();
    }

    public ImportStuff take() throws InterruptedException {
        return providerQueue.take();
    }

    public ImportStuff peek() {
        return providerQueue.peek();
    }

    public int getQueueSize() {
        return providerQueue.size();
    }

    private static class SingletonHolder {
        private static final StuffProviderQueue INSTANCE = new StuffProviderQueue();
    }

}
