package com.fr.swift.cluster.base.node;

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
     * @return 是否是备份节点
     */
    boolean isBackupNode();

    /**
     * @param isNodeMaster:是否为主节点
     */
    void setMaster(boolean isNodeMaster);

    String toString();

    int hashCode();
}
