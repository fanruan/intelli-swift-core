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

    public ClusterEvent(ClusterEventType eventType) {
        this.eventType = eventType;
    }

    public ClusterEventType getEventType() {
        return eventType;
    }
}
