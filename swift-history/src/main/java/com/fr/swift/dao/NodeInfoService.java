package com.fr.swift.dao;

import com.fr.swift.annotation.service.InnerService;
import com.fr.swift.config.entity.SwiftNodeInfo;
import com.fr.swift.db.NodeType;
import com.fr.swift.executor.task.bean.info.PlanningInfo;

import java.util.List;
import java.util.Set;

/**
 * @author Heng.J
 * @date 2020/10/27
 * @description 缓存node配置, 仅供主节点使用
 * @since swift-1.2.0
 */
@InnerService
public interface NodeInfoService {

    /**
     * 获取执行某个月份任务的节点
     */
    Set<String> getTaskTargets(String yearMonth);

    /**
     * 获取节点明细配置
     */
    SwiftNodeInfo getNodeInfo(String clusterId);

    /**
     * 获取可迁移节点
     */
    Set<String> getMigrateNodeIds();

    /**
     * 获取某一类型节点
     */
    Set<String> getNodeInfos(NodeType nodeType);

    /**
     * 获取节点计划任务信息
     */
    List<PlanningInfo> getMigrateInfos(String clusterId);

    /**
     * 节点是否可以接收任务
     */
    boolean isAcceptable(String clusterId);

    void clearCache();
}
