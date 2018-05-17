package com.fr.swift.cluster;

import com.fr.cluster.ClusterBridge;
import com.fr.cluster.core.ClusterNode;

/**
 * This class created on 2018/5/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ClusterNodeManager {

    public ClusterNode masterNode;
    public ClusterNode currentNode;

    private static final ClusterNodeManager INSTANCE = new ClusterNodeManager();

    private ClusterNodeManager() {
        this.currentNode = ClusterBridge.getView().getCurrent();
    }

    public static ClusterNodeManager getInstance() {
        return INSTANCE;
    }

    public void setMasterNode(ClusterNode masterNode) {
        this.masterNode = masterNode;
    }

    public void setCurrentNode(ClusterNode currentNode) {
        this.currentNode = currentNode;
    }

    public ClusterNode getMasterNode() {
        return masterNode;
    }

    public ClusterNode getCurrentNode() {
        return currentNode;
    }
}
