package com.fr.swift;

/**
 * This class created on 2018/6/13
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface ClusterNodeManager<T> {
    void setMasterNode(T masterNode);

    T getMasterNode();

    T getCurrentNode();

    String getCurrentId();

    String getMasterId();

    boolean isCluster();

    void setCluster(boolean cluster);

    boolean isMaster();
}
