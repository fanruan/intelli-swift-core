package com.fr.swift.event;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * This class created on 2019/1/3
 *
 * @author Lucifer
 * @description
 */
public class ClusterListenerHandlerTest extends TestCase {

    @Test
    public void testListener() {
        assertNotNull(ClusterListenerHandler.getInstance());
        ClusterEventListener listener1 = new ClusterEventListener() {
            @Override
            public void handleEvent(ClusterEvent clusterEvent) {
                if (clusterEvent.getEventType() == ClusterEventType.LEFT_CLUSTER && clusterEvent.getClusterType() == ClusterType.FR) {
                    throw new RuntimeException();
                }
            }
        };
        ClusterListenerHandler.addInitialListener(listener1);

        ClusterListenerHandler.handlerEvent(new ClusterEvent(ClusterEventType.JOIN_CLUSTER, ClusterType.FR));
        try {
            ClusterListenerHandler.handlerEvent(new ClusterEvent(ClusterEventType.LEFT_CLUSTER, ClusterType.FR));
            assertTrue(false);
        } catch (RuntimeException e) {
            assertTrue(true);
        }
        ClusterListenerHandler.removeInitialListener(listener1);
        ClusterListenerHandler.handlerEvent(new ClusterEvent(ClusterEventType.LEFT_CLUSTER, ClusterType.FR));

        ClusterListenerHandler.addExtraListener(listener1);
        ClusterListenerHandler.handlerEvent(new ClusterEvent(ClusterEventType.JOIN_CLUSTER, ClusterType.FR));
        try {
            ClusterListenerHandler.handlerEvent(new ClusterEvent(ClusterEventType.LEFT_CLUSTER, ClusterType.FR));
            assertTrue(false);
        } catch (RuntimeException e) {
            assertTrue(true);
        }
        ClusterListenerHandler.removeExtraListener(listener1);
        ClusterListenerHandler.handlerEvent(new ClusterEvent(ClusterEventType.LEFT_CLUSTER, ClusterType.FR));

    }
}
