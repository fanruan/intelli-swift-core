package com.fr.swift.event;

/**
 * This class created on 2018/5/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ClusterEvent implements Event {

    private ClusterEventType eventType;

    private ClusterType clusterType;

    public ClusterEvent(ClusterEventType eventType, ClusterType clusterType) {
        this.eventType = eventType;
        this.clusterType = clusterType;
    }

    public ClusterEventType getEventType() {
        return eventType;
    }

    public ClusterType getClusterType() {
        return clusterType;
    }
}
