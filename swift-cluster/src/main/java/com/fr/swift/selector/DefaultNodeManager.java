package com.fr.swift.selector;

import com.fr.swift.ClusterNodeManager;

/**
 * @author Marvin
 * @date 8/1/2019
 * @description
 * @since swift 1.1
 */
public enum DefaultNodeManager implements ClusterNodeManager {

    INSTANCE;

    @Override
    public void setMasterNode(Object masterNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getMasterNode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getCurrentNode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCurrentId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMasterId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCluster() {
        return false;
    }

    @Override
    public void setCluster(boolean cluster) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isMaster() {
        throw new UnsupportedOperationException();
    }
}
