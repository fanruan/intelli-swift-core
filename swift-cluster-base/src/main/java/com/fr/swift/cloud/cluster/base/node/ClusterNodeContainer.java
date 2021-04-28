package com.fr.swift.cloud.cluster.base.node;

import java.util.Map;

/**
 * @author lucifer
 * @date 2020/5/8
 * @description
 * @since swift 1.1
 */
public interface ClusterNodeContainer {
    Map<String, ClusterNode> getOnlineNodes();

    Map<String, ClusterNode> getHistoryNodes();

    Map<String, ClusterNode> getOfflineNodes();

    ClusterNode getMasterNode();

    ClusterNode getCurrentNode();
}
