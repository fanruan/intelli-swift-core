package com.fr.swift.dao;

import com.fr.swift.annotation.service.InnerService;
import com.fr.swift.config.entity.SwiftNodeInfo;
import com.fr.swift.db.NodeType;
import com.fr.swift.service.info.TaskInfo;

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
     * 清除所有缓存
     */
    void clearCache();

    /**
     * 刷新所有缓存
     */
    void flushCache();

    /**
     * 获取全部阻塞的blockIndex
     */
    Set<String> getBlockIndexes();

    /**
     * 获取各个节点阻塞blockIndex
     */
    String getBlockIndexById(String clusterId);

    /**
     * 获取某个blockIndex下的ids
     */
    List<String> getIdsByBlockIndex(String blockIndex);

    /**
     * 获取某个节点准备状态
     */
    int getReadyStatusById(String clusterId);

    /**
     * 更新某个节点迁移准备状态
     */
    void updateReadyStatusById(String clusterId, int status);

    /**
     * 获取某个节点计划的迁移indexes
     */
    List<String> getPreMigIndexById(String clusterId);

    /**
     * 获取节点明细配置
     */
    SwiftNodeInfo getNodeInfoById(String clusterId);

    /**
     * 获取某一类型节点
     */
    Set<String> getNodeInfosByType(NodeType nodeType);

    /**
     * 获取节点迁移计划任务信息
     */
    List<TaskInfo> getMigrateInfosById(String clusterId);

    /**
     * 获取可迁移节点
     */
    Set<String> getMigrateNodeIds();

    /**
     * 获取执行某个migIndex任务的节点
     */
    Set<String> getTaskTargets(String migIndex);

    /**
     * 激活blockIndex对应节点
     */
    void activateNodeMigIndex(String clusterId, String blockIndex);

    /**
     * 阻塞blockIndex对应节点
     */
    void blockNodeMigIndex(String clusterId, String blockIndex);

    /**
     * 对blockIndex的所有节点非RUNNING则更新为空
     */
    void updateBlockMigIndex(String blockIndex);
}
