package com.fr.swift.cloud.cluster.base.handler;

import com.fr.swift.cloud.cluster.base.event.ClusterEvent;
import com.fr.swift.cloud.event.SwiftEventDispatcher;

/**
 * This class created on 2020/4/26
 *
 * @author Kuifang.Liu
 */
public class LeftClusterListenerHandler extends ClusterListenerHandler {
    @Override
    public void on(ClusterEventData data) {
    }

    static {
        SwiftEventDispatcher.listen(ClusterEvent.LEFT, new LeftClusterListenerHandler());
    }

    public static void listen() {
    }
}
