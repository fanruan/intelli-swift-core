package com.fr.swift.cloud.cluster.base.service;

import com.fr.swift.cloud.cluster.base.node.ClusterNode;

/**
 * This class created on 2020/4/26
 *
 * @author Kuifang.Liu
 */
public interface ClusterRegistryService {

    void competeAndInit();

    boolean competeMaster();

    void registerNode(ClusterNode node);

    void unRegisterNode(ClusterNode node);
}
