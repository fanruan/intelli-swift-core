package com.fr.swift.cluster.base.node;


/**
 * This class created on 2020/4/13
 *
 * @author Kuifang.Liu
 */
public class SwiftClusterNodeImpl implements ClusterNode {
    private String nodeId;
    private String address;
    private boolean isMaster;
    private String ip;
    private int port;
    private boolean isBackupNode;

    public SwiftClusterNodeImpl(String nodeId, String nodeAddress, boolean isBackupNode) {
        this.address = nodeAddress;
        this.nodeId = nodeId;
        this.isMaster = false;
        String[] addressArr = address.split(":");
        this.ip = addressArr[0];
        this.port = Integer.parseInt(addressArr[1]);
        this.isBackupNode = isBackupNode;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String getId() {
        return nodeId;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public boolean isMaster() {
        return isMaster;
    }

    @Override
    public boolean isBackupNode() {
        return isBackupNode;
    }

    @Override
    public void setMaster(boolean isNodeMaster) {
        isMaster = isNodeMaster;
    }

    @Override
    public String toString() {
        return "Node " + nodeId + "  " + address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SwiftClusterNodeImpl that = (SwiftClusterNodeImpl) o;
        return isMaster == that.isMaster &&
                nodeId.equals(that.nodeId) &&
                address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return (nodeId + address).hashCode();
    }
}

