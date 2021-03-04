package com.fr.swift.cloud.cluster.base.handler;

import com.fr.swift.cloud.event.SwiftEventListener;

/**
 * This class created on 2020/4/24
 *
 * @author Kuifang.Liu
 */
public class ClusterListenerHandler implements SwiftEventListener<ClusterListenerHandler.ClusterEventData> {

    public void on(ClusterEventData data) {
    }


    public static class ClusterEventData {
        String clusterId;

        public ClusterEventData(String id) {
            this.clusterId = id;
        }

        public String getClusterId() {
            return clusterId;
        }
    }
}
