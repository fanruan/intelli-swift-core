package com.fr.swift.event;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/5/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ClusterListenerHandler {

    private List<ClusterEventListener> clusterEventListeners;

    private static final ClusterListenerHandler INSTANCE = new ClusterListenerHandler();

    public static ClusterListenerHandler getInstance() {
        return INSTANCE;
    }

    private ClusterListenerHandler() {
        this.clusterEventListeners = new ArrayList<ClusterEventListener>();
    }

    public static void handlerEvent(ClusterEvent clusterEvent) {
        for (ClusterEventListener clusterEventListener : getInstance().clusterEventListeners) {
            clusterEventListener.handleEvent(clusterEvent);
        }
    }

    public static void addListener(ClusterEventListener clusterEventListener) {
        getInstance().clusterEventListeners.add(clusterEventListener);
    }
}
