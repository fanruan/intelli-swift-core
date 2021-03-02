package com.fr.swift.cloud.cluster.base.handler;

import com.fr.swift.cloud.cluster.base.event.ClusterEvent;
import com.fr.swift.cloud.event.SwiftEventDispatcher;

/**
 * This class created on 2020/4/26
 *
 * @author Kuifang.Liu
 */
public class JoinClusterListenerHandler extends ClusterListenerHandler {
    @Override
    public void on(ClusterEventData data) {
//        MasterServiceInitiator.getInstance().initByPriority(data);
    }

    static {
        SwiftEventDispatcher.listen(ClusterEvent.JOIN, new JoinClusterListenerHandler());
    }

    public static void listen() {
    }
}
