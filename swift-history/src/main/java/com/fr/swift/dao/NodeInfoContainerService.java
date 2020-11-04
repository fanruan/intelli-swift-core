package com.fr.swift.dao;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.entity.SwiftNodeInfo;
import com.fr.swift.db.NodeType;
import com.fr.swift.executor.task.bean.info.PlanningInfo;

import java.util.List;
import java.util.Set;

/**
 * @author Heng.J
 * @date 2020/10/27
 * @description
 * @since swift-1.2.0
 */
@SwiftBean(name = "nodeInfoContainerService")
public class NodeInfoContainerService implements NodeInfoService {

    @Override
    public Set<String> getTaskTargets(String yearMonth) {
        return NodeInfoContainer.MASTER.getTaskTargets(yearMonth);
    }

    @Override
    public SwiftNodeInfo getNodeInfo(String clusterId) {
        return NodeInfoContainer.MASTER.getNodeInfo(clusterId);
    }

    @Override
    public Set<String> getMigrateNodeIds() {
        return NodeInfoContainer.MASTER.getMigrateNodeIds();
    }

    @Override
    public Set<String> getNodeInfos(NodeType nodeType) {
        return NodeInfoContainer.MASTER.getNodeInfos(nodeType);
    }

    @Override
    public List<PlanningInfo> getMigrateInfos(String clusterId) {
        return NodeInfoContainer.MASTER.getMigrateInfos(clusterId);
    }

    @Override
    public boolean isAcceptable(String clusterId) {
        return NodeInfoContainer.MASTER.isAcceptable(clusterId);
    }

    @Override
    public void clearCache() {
        NodeInfoContainer.MASTER.clearCache();
    }
}
