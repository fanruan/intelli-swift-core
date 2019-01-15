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

    private List<ClusterEventListener> initialEventListeners;

    private List<ClusterEventListener> extraEventListeners;

    private static ReentrantLock lock = new ReentrantLock();

    private static final ClusterListenerHandler INSTANCE = new ClusterListenerHandler();

    public static ClusterListenerHandler getInstance() {
        return INSTANCE;
    }

    private ClusterListenerHandler() {
        this.initialEventListeners = new ArrayList<ClusterEventListener>();
        this.extraEventListeners = new ArrayList<ClusterEventListener>();
    }

    public static void handlerEvent(ClusterEvent clusterEvent) {
        lock.lock();
        try {
            for (ClusterEventListener initialEventListener : INSTANCE.initialEventListeners) {
                initialEventListener.handleEvent(clusterEvent);
            }
            for (ClusterEventListener extraEventListener : INSTANCE.extraEventListeners) {
                extraEventListener.handleEvent(clusterEvent);
            }
        } finally {
            lock.unlock();
        }
    }

    public static void addInitialListener(ClusterEventListener clusterEventListener) {
        lock.lock();
        try {
            if (!INSTANCE.initialEventListeners.contains(clusterEventListener)) {
                INSTANCE.initialEventListeners.add(clusterEventListener);
            }
        } finally {
            lock.unlock();
        }
    }

    public static void addExtraListener(ClusterEventListener clusterEventListener) {
        lock.lock();
        try {
            if (!INSTANCE.extraEventListeners.contains(clusterEventListener)) {
                INSTANCE.extraEventListeners.add(clusterEventListener);
            }
        } finally {
            lock.unlock();
        }
    }

    public static void removeInitialListener(ClusterEventListener clusterEventListener) {
        lock.lock();
        try {
            INSTANCE.initialEventListeners.remove(clusterEventListener);
        } finally {
            lock.unlock();
        }
    }

    public static void removeExtraListener(ClusterEventListener clusterEventListener) {
        lock.lock();
        try {
            INSTANCE.extraEventListeners.remove(clusterEventListener);
        } finally {
            lock.unlock();
        }
    }
}
