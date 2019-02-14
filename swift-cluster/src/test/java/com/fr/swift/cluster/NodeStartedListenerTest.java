package com.fr.swift.cluster;

import com.fr.swift.cluster.listener.NodeStartedListener;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterType;
import junit.framework.TestCase;

/**
 * This class created on 2019/1/3
 *
 * @author Lucifer
 * @description
 */
public class NodeStartedListenerTest extends TestCase {

    public void testListener() {
        NodeStartedListener.INSTANCE.registerTask(new NodeStartedListener.NodeStartedTask() {
            @Override
            public void run() {
                throw new RuntimeException();
            }
        });

        try {
            NodeStartedListener.INSTANCE.handleEvent(new ClusterEvent(ClusterEventType.JOIN_CLUSTER, ClusterType.FR));
            assertTrue(false);
        } catch (RuntimeException e) {
            assertTrue(true);
        }

        try {
            NodeStartedListener.INSTANCE.handleEvent(new ClusterEvent(ClusterEventType.LEFT_CLUSTER, ClusterType.FR));
            assertTrue(true);
        } catch (RuntimeException e) {
            assertTrue(false);
        }
    }
}
