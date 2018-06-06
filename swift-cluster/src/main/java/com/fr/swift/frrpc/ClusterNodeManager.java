package com.fr.swift.frrpc;

import com.fr.cluster.ClusterBridge;
import com.fr.cluster.core.ClusterNode;
import com.fr.general.ComparatorUtils;

/**
 * This class created on 2018/5/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ClusterNodeManager {

    private ClusterNode masterNode;
    private ClusterNode currentNode;
    private boolean isCluster;

    private static final ClusterNodeManager INSTANCE = new ClusterNodeManager();

    private ClusterNodeManager() {
        this.currentNode = ClusterBridge.getView().getCurrent();
        this.isCluster = false;
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

    public String getCurrentId() {
        return currentNode.getID();
    }

    public String getMasterId() {
        if (masterNode == null) {
            return null;
        }
        return masterNode.getID();
    }

    public boolean isCluster() {
        synchronized (ClusterNodeManager.class) {
            return isCluster;
        }
    }

    public void setCluster(boolean cluster) {
        synchronized (ClusterNodeManager.class) {
            isCluster = cluster;
        }
    }

    public boolean isMaster() {
        synchronized (ClusterNodeManager.class) {
            if (masterNode == null) {
                return false;
            }
            return ComparatorUtils.equals(getMasterId(), getCurrentId());
        }
    }
}
