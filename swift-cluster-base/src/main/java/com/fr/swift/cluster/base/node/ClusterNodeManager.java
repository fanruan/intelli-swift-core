package com.fr.swift.cluster.base.node;

import java.util.Map;

/**
 * This class created on 2020/4/26
 *
 * @author Kuifang.Liu
 */
public interface ClusterNodeManager extends ClusterNodeContainer {

    void setMasterNode(String masterNodeId, String masterNodeAddress);

    void setCurrentNode(String currentNodeId, String currentNodeAddress, boolean isBackupNode);

    void putHistoryNode(String historyNodeId, String historyNodeAddress, boolean isBackupNode);

    void handleNodeChange(Map<String, String> nodes);

}