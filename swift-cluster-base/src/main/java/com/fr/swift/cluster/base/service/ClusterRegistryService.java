package com.fr.swift.cluster.base.service;

import com.fr.swift.cluster.base.node.ClusterNode;

/**
 * This class created on 2020/4/26
 *
 * @author Kuifang.Liu
 */
public interface ClusterRegistryService {
    void competeMaster();

    void registerNode(ClusterNode node);

    void unRegisterNode(ClusterNode node);
}
