package com.fr.swift.manager;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.manager.IndexStuffPorvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class created on 2017-12-19 15:02:47
 *
 * @author Lucifer
 * @description 临时管理所有provider的manager单例
 * @since Advanced FineBI Analysis 1.0
 */
public class ProviderManager {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ProviderManager.class);

    private static int MAX_CAPACITY = 10000;

    private BlockingQueue<IndexStuffPorvider> porviderQueue = new LinkedBlockingQueue<IndexStuffPorvider>(MAX_CAPACITY);

    private ProviderManager() {
        providerMap = new HashMap<Long, List<IndexStuffPorvider>>();
    }

    private static class SingletonHolder {
        private static final ProviderManager INSTANCE = new ProviderManager();
    }

    public static final ProviderManager getManager() {
        return SingletonHolder.INSTANCE;
    }

    private Map<Long, List<IndexStuffPorvider>> providerMap;

    private Object lock = new Object();

    public synchronized void registProvider(long userId, IndexStuffPorvider provider) {
        try {
            if (provider != null) {
                if (providerMap.containsKey(userId)) {
                    providerMap.get(userId).add(provider);
                } else {
                    List<IndexStuffPorvider> providerList = new ArrayList<IndexStuffPorvider>();
                    providerList.add(provider);
                    providerMap.put(userId, providerList);
                }
                synchronized (lock) {
                    porviderQueue.put(provider);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public IndexStuffPorvider poll() {
        synchronized (lock) {
            return porviderQueue.poll();
        }
    }

    public IndexStuffPorvider take() throws InterruptedException {
        return porviderQueue.take();
    }

    public IndexStuffPorvider peek() {
        synchronized (lock) {
            return porviderQueue.peek();
        }
    }
}
