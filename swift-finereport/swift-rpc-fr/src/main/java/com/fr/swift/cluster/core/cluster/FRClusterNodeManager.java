package com.fr.swift.cluster.core.cluster;

import com.fr.cluster.ClusterBridge;
import com.fr.cluster.core.ClusterNode;
import com.fr.general.ComparatorUtils;
import com.fr.swift.ClusterNodeManager;

/**
 * This class created on 2018/5/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class FRClusterNodeManager implements ClusterNodeManager<ClusterNode> {

    private ClusterNode masterNode;
    private final ClusterNode currentNode;
    private boolean isCluster;

    private static final FRClusterNodeManager INSTANCE = new FRClusterNodeManager();

    private FRClusterNodeManager() {
        this.currentNode = ClusterBridge.getView().getCurrent();
        this.isCluster = false;
    }

    public static FRClusterNodeManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void setMasterNode(ClusterNode masterNode) {
        this.masterNode = masterNode;
    }

    @Override
    public ClusterNode getMasterNode() {
        return masterNode;
    }

    @Override
    public ClusterNode getCurrentNode() {
        return currentNode;
    }

    @Override
    public String getCurrentId() {
        return currentNode.getID();
    }

    @Override
    public String getMasterId() {
        if (masterNode == null) {
            return null;
        }
        return masterNode.getID();
    }

    @Override
    public boolean isCluster() {
        synchronized (FRClusterNodeManager.class) {
            return isCluster;
        }
    }

    @Override
    public void setCluster(boolean cluster) {
        synchronized (FRClusterNodeManager.class) {
            isCluster = cluster;
        }
    }

    @Override
    public boolean isMaster() {
        synchronized (FRClusterNodeManager.class) {
            if (masterNode == null) {
                return false;
            }
            return ComparatorUtils.equals(getMasterId(), getCurrentId());
        }
    }
}
