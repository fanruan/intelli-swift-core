package com.fr.swift.event;

import java.util.EventListener;

/**
 * This class created on 2018/5/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface ClusterEventListener extends EventListener {
    void handleEvent(ClusterEvent clusterEvent);
}
