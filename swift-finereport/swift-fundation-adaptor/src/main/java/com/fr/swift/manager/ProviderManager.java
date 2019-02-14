package com.fr.swift.manager;

import com.fr.swift.cube.queue.ProviderTaskManager;
import com.fr.swift.cube.queue.StuffProviderQueue;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.manager.IndexStuffProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2017-12-19 15:02:47
 *
 * @author Lucifer
 * @description 临时管理所有provider的manager单例
 * @since Advanced FineBI Analysis 1.0
 */
public class ProviderManager {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ProviderManager.class);

    private ProviderManager() {
        providerMap = new HashMap<Long, List<IndexStuffProvider>>();
        ProviderTaskManager.start();
    }

    private static class SingletonHolder {
        private static final ProviderManager INSTANCE = new ProviderManager();
    }

    public static final ProviderManager getManager() {
        return SingletonHolder.INSTANCE;
    }

    private Map<Long, List<IndexStuffProvider>> providerMap;

    private Object lock = new Object();

    public synchronized void registProvider(long userId, IndexStuffProvider provider) {
        try {
            if (provider != null) {
                if (providerMap.containsKey(userId)) {
                    providerMap.get(userId).add(provider);
                } else {
                    List<IndexStuffProvider> providerList = new ArrayList<IndexStuffProvider>();
                    providerList.add(provider);
                    providerMap.put(userId, providerList);
                }
                synchronized (lock) {
                    StuffProviderQueue.getQueue().put(provider);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
