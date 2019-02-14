package com.fr.swift.cluster.listener;

import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/9/26
 */
public enum NodeStartedListener implements ClusterEventListener {
    /**
     * Singleton
     */
    INSTANCE;

    private List<NodeStartedTask> tasks = new ArrayList<NodeStartedTask>();

    NodeStartedListener() {
    }

    public void registerTask(NodeStartedTask task) {
        if (null != task) {
            tasks.add(task);
        }
    }

    @Override
    public void handleEvent(ClusterEvent clusterEvent) {
        if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
            for (NodeStartedTask task : tasks) {
                task.run();
            }
        }
    }

    public interface NodeStartedTask {
        /**
         * run task
         */
        void run();
    }
}
