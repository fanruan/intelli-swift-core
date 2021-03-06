package com.fr.swift.cloud.dao;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.config.entity.SwiftNodeInfo;
import com.fr.swift.cloud.config.service.SwiftNodeInfoService;
import com.fr.swift.cloud.db.MigrateType;
import com.fr.swift.cloud.db.NodeType;
import com.fr.swift.cloud.executor.task.bean.MigrateBean;
import com.fr.swift.cloud.executor.task.info.MigScheduleInfo;
import com.fr.swift.cloud.executor.task.info.interval.MigInterval;
import com.fr.swift.cloud.service.info.TaskInfo;
import com.fr.swift.cloud.util.Strings;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Heng.J
 * @date 2020/10/27
 * @description 仅主节点从数据库读取和维持缓存, 当前仅支持一个节点一种迁移配置
 * @since swift-1.2.0
 */
public enum NodeInfoContainer implements NodeInfoService {

    /**
     * 更新缓存:
     * 1、竞争成为主节点,全部更新
     * 2、收到激活任务信息,部分更新
     * 更新数据库:
     * 1、接收过block且掉线,条件下部分更新
     */
    MASTER;

    private final SwiftNodeInfoService nodeInfoService = SwiftContext.get().getBean(SwiftNodeInfoService.class);

    // 节点id(swift.properties) : 节点
    private final Map<String, SwiftNodeInfo> idNodeMap = Maps.newConcurrentMap();
    // 节点id : MigInterval
    private final Map<String, MigInterval> idMigIntervalMap = Maps.newConcurrentMap();
    // 节点id : 阻塞index
    private final Map<String, String> idBlockIndexMap = Maps.newConcurrentMap();
    // 节点id : readyStatus
    private final Map<String, Integer> idReadyStatusMap = Maps.newConcurrentMap();
    // 结点类型 : 节点id
    private final Map<NodeType, Set<String>> nodeTypeIdMap = Maps.newConcurrentMap();
    // 月份  : 节点id
    private final Map<String, Set<String>> indexReceivedIdMap = Maps.newConcurrentMap();

    private void initCache(List<SwiftNodeInfo> nodeInfoList) {
        nodeInfoList.forEach(nodeInfo -> {
            String nodeId = nodeInfo.getNodeId();
            idNodeMap.put(nodeId, nodeInfo);
            idMigIntervalMap.put(nodeId, MigInterval.getMigrateInterval(nodeInfo));
            idBlockIndexMap.put(nodeId, nodeInfo.getBlockingIndex());
            idReadyStatusMap.put(nodeId, nodeInfo.getReadyStatus());
            nodeTypeIdMap.computeIfAbsent(nodeInfo.getNodeType(), k -> new HashSet<>()).add(nodeInfo.getNodeId());
            if (nodeInfo.getNodeType().isMigratable()) {
                List<String> indexCoverRange = idMigIntervalMap.get(nodeId).getIndexCoverRange();
                indexCoverRange.forEach(index -> indexReceivedIdMap.computeIfAbsent(index, k -> new HashSet<>()).add(nodeId));
            }
        });
    }

    @Override
    public Set<String> getBlockIndexes() {
        return idBlockIndexMap.values().stream().filter(index -> !Strings.isEmpty(index)).collect(Collectors.toSet());
    }

    @Override
    public String getBlockIndexById(String clusterId) {
        return idBlockIndexMap.getOrDefault(clusterId, Strings.EMPTY);
    }

    @Override
    public List<String> getIdsByBlockIndex(String blockIndex) {
        return idBlockIndexMap.entrySet().stream().filter(entry -> entry.getValue().equals(blockIndex)).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    @Override
    public int getReadyStatusById(String clusterId) {
        return idReadyStatusMap.get(clusterId);
    }

    @Override
    public void updateReadyStatusById(String clusterId, int status) {
        SwiftNodeInfo nodeInfo = idNodeMap.get(clusterId);
        nodeInfo.setReadyStatus(status);
        nodeInfoService.update(nodeInfo);
        idReadyStatusMap.put(clusterId, status);
    }

    @Override
    public List<String> getPreMigIndexById(String clusterId) {
        return idMigIntervalMap.get(clusterId).getPreMigIndex();
    }

    @Override
    public SwiftNodeInfo getNodeInfoById(String clusterId) {
        return idNodeMap.get(clusterId);
    }

    @Override
    public Set<String> getNodeInfosByType(NodeType nodeType) {
        return nodeTypeIdMap.getOrDefault(nodeType, Collections.emptySet());
    }

    @Override
    public List<TaskInfo> getMigrateInfosById(String clusterId) {
        List<TaskInfo> result = new ArrayList<>();
        if (idNodeMap.containsKey(clusterId)) {
            SwiftNodeInfo nodeInfo = idNodeMap.get(clusterId);
            MigrateType migrateType = nodeInfo.getMigrateType();
            if (nodeInfo.getNodeType().isMigratable() && migrateType.needMigrated()) {
                if (migrateType.equals(MigrateType.WAITING)) {
                    result = idMigIntervalMap.get(clusterId).getPreMigIndex().stream()
                            .map(migIndex -> new MigScheduleInfo(nodeInfo.getMigrateTime(), MigrateBean.of(migIndex, nodeInfo.getMigrateTarget())))
                            .collect(Collectors.toList());
                } else {
                    String migrateTime = addDay(nodeInfo.getMigrateTime(), migrateType.getTimes());
                    result = idMigIntervalMap.get(clusterId).getPreMigIndex().stream()
                            .map(migIndex -> new MigScheduleInfo(migrateTime, MigrateBean.of(migIndex, nodeInfo.getMigrateTarget())))
                            .collect(Collectors.toList());
                }
            }
        }
        return result;
    }

    @Override
    public Set<String> getMigrateNodeIds() {
        return getNodeInfosByType(NodeType.TASK);
    }

    @Override
    public Set<String> getTaskTargets(String migIndex) {
        return indexReceivedIdMap.get(migIndex);
    }

    @Override
    public void activateNodeMigIndex(String clusterId, String blockIndex) {
        // 刷新触发节点缓存
        flushCacheById(clusterId);
        flushCacheById(idNodeMap.get(clusterId).getMigrateTarget());
        // 检查其他,都完成则更新非迁移 blockIndex
        updateBlockMigIndex(blockIndex);
    }

    @Override
    public void blockNodeMigIndex(String clusterId, String blockIndex) {
        // block触发节点
        SwiftNodeInfo nodeInfo = idNodeMap.get(clusterId);
        nodeInfo.setMigrateType(MigrateType.RUNNING);
        updateBlockMigIndex(clusterId, blockIndex);
        // block其他同index节点
        indexReceivedIdMap.get(blockIndex).stream()
                .filter(cluster -> idBlockIndexMap.get(cluster).isEmpty())
                .forEach(cluster -> updateBlockMigIndex(cluster, blockIndex));
    }

    @Override
    public void updateBlockMigIndex(String blockIndex) {
        boolean isAllFinished = indexReceivedIdMap.get(blockIndex).stream()
                .map(cluster -> !idNodeMap.get(cluster).getMigrateType().equals(MigrateType.RUNNING))
                .reduce(true, (a, b) -> a && b);
        if (isAllFinished) {
            for (String cluster : indexReceivedIdMap.get(blockIndex)) {
                updateBlockMigIndex(cluster, Strings.EMPTY);
                updateReadyStatusById(cluster, 0);
            }
        }
    }

    private void updateBlockMigIndex(String clusterId, String blockIndex) {
        SwiftNodeInfo nodeInfo = idNodeMap.get(clusterId);
        nodeInfo.setBlockingIndex(blockIndex);
        nodeInfoService.update(nodeInfo);
        idBlockIndexMap.put(clusterId, blockIndex);
    }

    @Override
    public void clearCache() {
        clear();
    }

    @Override
    public void flushCache() {
        clear();
        initCache(nodeInfoService.getAllNodeInfo());
    }

    private void flushCacheById(String clusterId) {
        clearCacheById(clusterId);
        initCache(Collections.singletonList(nodeInfoService.getNodeInfo(clusterId)));
    }

    private void clearCacheById(String clusterId) {
        idNodeMap.remove(clusterId);
        idMigIntervalMap.remove(clusterId);
        idBlockIndexMap.remove(clusterId);
        idReadyStatusMap.remove(clusterId);
        nodeTypeIdMap.values().forEach(k -> k.removeIf(v -> v.contains(clusterId)));
        indexReceivedIdMap.values().forEach(k -> k.removeIf(v -> v.contains(clusterId)));
    }

    private void clear() {
        idNodeMap.clear();
        idMigIntervalMap.clear();
        idBlockIndexMap.clear();
        idReadyStatusMap.clear();
        indexReceivedIdMap.clear();
        nodeTypeIdMap.clear();
    }

    /**
     * 修改corn时间增加指定天数
     *
     * @param cronExpression 形如 "0 0 1 15 * ?" 确保日位为单一数字
     * @param day
     * @return "0 0 1 16 x ?" x为当前月
     */
    private static String addDay(String cronExpression, int day) {
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        String[] cornPart = cronExpression.split("\\s+");
        cornPart[3] = String.valueOf(Integer.parseInt(cornPart[3]) + day);
        String sep = " ";
        return Arrays.stream(cornPart, 0, 4).collect(Collectors.joining(sep, "", sep + month + " ?"));
    }
}
