package com.fr.swift.cluster.base.node;

import java.util.Map;

/**
 * This class created on 2020/4/26
 *
 * @author Kuifang.Liu
 */
public interface ClusterNodeManager {
    Map<String, ClusterNode> getOnlineNodes();

    Map<String, ClusterNode> getHistoryNodes();

    Map<String, ClusterNode> getOfflineNodes();

    void setMasterNode(String masterNodeId, String masterNodeAddress);

    ClusterNode getMasterNode();

    void setCurrentNode(String currentNodeId, String currentNodeAddress);

    ClusterNode getCurrentNode();

    void handleNodeChange(Map<String, String> nodes);
}
