package com.fr.swift.cube.queue;

import com.fr.swift.util.concurrent.SwiftExecutors;

/**
 * This class created on 2018-1-16 16:51:49
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class ProviderTaskManager {

    private ProviderTaskManager() {
        initListener();
        SwiftExecutors.newThread(new StuffFetcher()).start();
    }

    private static class SingletonHolder {
        private static final ProviderTaskManager INSTANCE = new ProviderTaskManager();
    }

    public static void start() {
        ProviderTaskManager.getManager();
    }

    public static ProviderTaskManager getManager() {
        return SingletonHolder.INSTANCE;
    }

    private void initListener() {
    }
}