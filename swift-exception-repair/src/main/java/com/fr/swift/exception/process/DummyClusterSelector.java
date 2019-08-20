package com.fr.swift.exception.process;

import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.service.ServiceType;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 这个类用来测试异常处理流程
 *
 * @author Marvin
 * @date 8/14/2019
 * @description
 * @since swift 1.1
 */
public class DummyClusterSelector extends OperateClusterSelector {

    private static final DummyClusterSelector INSTANCE = new DummyClusterSelector();

    public static DummyClusterSelector getInstance() {
        return INSTANCE;
    }

    @Override
    public String selectCluster(ExceptionInfo info) {
        Set<String> clusterIds = new HashSet<String>();
        //根据不同的异常类型选择提供某种服务的节点来处理异常
        clusterIds.addAll(ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE).keySet());
        if (!occupiedNodes.isEmpty()) {
            clusterIds.removeAll(occupiedNodes);
        }
        Iterator<String> it = clusterIds.iterator();
        if (it.hasNext()) {
            String nodeId = it.next();
            occupiedNodes.add(nodeId);
            return nodeId;
        } else {
            return null;
        }
    }
}
