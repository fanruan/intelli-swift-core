package com.fr.swift.cluster.base.event;

import com.fr.swift.event.SwiftEvent;

/**
 * This class created on 2020/4/26
 *
 * @author Kuifang.Liu
 */
public enum ClusterEvent implements SwiftEvent {
    /**
     * 加入集群
     */
    JOIN,
    /**
     * 离开集群
     */
    LEFT
}
