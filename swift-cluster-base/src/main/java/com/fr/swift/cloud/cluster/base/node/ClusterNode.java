package com.fr.swift.cloud.cluster.base.node;

/**
 * This class created on 2020/4/26
 *
 * @author Kuifang.Liu
 */
public interface ClusterNode {
    String getId();

    String getAddress();

    String getIp();

    int getPort();

    /**
     * @return 是否为主节点
     */
    boolean isMaster();

    /**
     * @param isNodeMaster:是否为主节点
     */
    void setMaster(boolean isNodeMaster);

    String toString();

    int hashCode();
}
