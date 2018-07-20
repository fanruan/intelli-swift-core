package com.fr.swift.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class created on 2018/5/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ClusterListenerHandler {

    private List<ClusterEventListener> clusterEventListeners;

    private static ReentrantLock lock = new ReentrantLock();

    private static final ClusterListenerHandler INSTANCE = new ClusterListenerHandler();

    public static ClusterListenerHandler getInstance() {
        return INSTANCE;
    }

    private ClusterListenerHandler() {
        this.clusterEventListeners = new ArrayList<ClusterEventListener>();
    }

    public static void handlerEvent(ClusterEvent clusterEvent) {
        lock.lock();
        try {
            for (ClusterEventListener clusterEventListener : getInstance().clusterEventListeners) {
                clusterEventListener.handleEvent(clusterEvent);
            }
        } finally {
            lock.unlock();
        }
    }

    public static void addListener(ClusterEventListener clusterEventListener) {
        lock.lock();
        try {
            getInstance().clusterEventListeners.add(clusterEventListener);
        } finally {
            lock.unlock();
        }
    }
}
