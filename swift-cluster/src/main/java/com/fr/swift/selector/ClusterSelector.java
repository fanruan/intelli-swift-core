package com.fr.swift.selector;

import com.fr.swift.ClusterNodeManager;
import com.fr.swift.basics.Selector;

/**
 * This class created on 2018/6/13
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ClusterSelector implements Selector<ClusterNodeManager> {

    private ClusterNodeManager clusterNodeManager;

    private ClusterSelector() {
    }

    private static final ClusterSelector INSTANCE = new ClusterSelector();

    public static ClusterSelector getInstance() {
        return INSTANCE;
    }

    @Override
    public ClusterNodeManager getFactory() {
        synchronized (ClusterSelector.class) {
            return this.clusterNodeManager;
        }
    }

    @Override
    public void switchFactory(ClusterNodeManager clusterNodeManager) {
        synchronized (ClusterSelector.class) {
            this.clusterNodeManager = clusterNodeManager;
        }
    }
}
