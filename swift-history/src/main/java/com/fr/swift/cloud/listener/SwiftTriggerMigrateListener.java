package com.fr.swift.cloud.listener;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.dao.NodeInfoService;
import com.fr.swift.cloud.db.MigrateType;
import com.fr.swift.cloud.event.SwiftEventDispatcher;
import com.fr.swift.cloud.executor.config.ExecutorTaskService;
import com.fr.swift.cloud.executor.task.info.MigTriggerInfo;
import com.fr.swift.cloud.executor.task.type.MigTaskType;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.service.TaskService;
import com.fr.swift.cloud.service.event.NodeEvent;
import com.fr.swift.cloud.service.event.NodeMessage;
import com.fr.swift.cloud.util.TimeUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Heng.J
 * @date 2020/12/3
 * @description
 * @since swift-1.2.0
 */
public class SwiftTriggerMigrateListener implements MigStrategyListener {

    private static final SwiftTriggerMigrateListener LISTENER = new SwiftTriggerMigrateListener();

    private static final ExecutorTaskService EXECUTOR_TASK_SERVICE = SwiftContext.get().getBean(ExecutorTaskService.class);

    private static final NodeInfoService NODE_INFO_SERVICE = SwiftContext.get().getBean(NodeInfoService.class);

    private static final TaskService TASK_SERVICE = SwiftContext.get().getBean(TaskService.class);

    public static void listen() {
        SwiftEventDispatcher.listen(NodeEvent.START_WAITING, LISTENER);
        checkAllAndDispatch();
    }

    public static void remove() {
        SwiftEventDispatcher.remove(LISTENER);
    }

    @Override
    public void on(NodeMessage nodeMessage) {
        String clusterId = nodeMessage.getClusterId();
        String migIndex = nodeMessage.getMessageInfo();
        SwiftLoggers.getLogger().info("Start to confirm the ready status of node {} related nodes", clusterId);
        NODE_INFO_SERVICE.updateReadyStatusById(clusterId, 1);
        retryAndDispatch(NODE_INFO_SERVICE.getIdsByBlockIndex(migIndex));
    }

    /**
     * 对所有节点 检查分发MIGRATE_TRIGGER任务
     */
    private static void checkAllAndDispatch() {
        Set<String> blockIndexSet = NODE_INFO_SERVICE.getBlockIndexes();
        blockIndexSet.stream()
                .filter(blockIndex -> needToDispatch(blockIndex))
                .forEach(blockIndex -> retryAndDispatch(NODE_INFO_SERVICE.getIdsByBlockIndex(blockIndex)));
    }

    private static boolean needToDispatch(String blockIndex) {
        List<String> clusterIds = NODE_INFO_SERVICE.getIdsByBlockIndex(blockIndex);
        boolean isAllReady = clusterIds.size() == clusterIds.stream().mapToInt(NODE_INFO_SERVICE::getReadyStatusById).sum();
        if (isAllReady) { //TODO 任务归属节点+任务执行状态
            return EXECUTOR_TASK_SERVICE.getMigRelatedTasks(TimeUtils.firstTimeOfDay(new Date()).getTime(), System.currentTimeMillis(), MigTaskType.MIGRATE_TRIGGER.name(), blockIndex).isEmpty();
        }
        return false;
    }

    private static void retryAndDispatch(List<String> clusterIds) {
        Map<String, Integer> readyStatusMap = clusterIds.stream()
                .collect(Collectors.toMap(cluster -> cluster, NODE_INFO_SERVICE::getReadyStatusById, (a, b) -> b));
        int count = readyStatusMap.values().stream().mapToInt(value -> value).sum();
        if (count == clusterIds.size()) {
            dispatchTriggerTasks(clusterIds);
        } else {
            List<String> notReadyIds = readyStatusMap.entrySet().stream().filter(entry -> entry.getValue() == 0).map(Map.Entry::getKey).collect(Collectors.toList());
            SwiftLoggers.getLogger().info("Waiting for node {} to complete migration preparation.", notReadyIds.toString());
        }
    }

    private static void dispatchTriggerTasks(Collection<String> clusters) {
        for (String id : clusters) {
            if (NODE_INFO_SERVICE.getNodeInfoById(id).getMigrateType().equals(MigrateType.RUNNING)) {
                SwiftLoggers.getLogger().info("Start to trigger {} 's migrate job", id);
                TASK_SERVICE.distributeTask(new MigTriggerInfo(NODE_INFO_SERVICE.getBlockIndexById(id)), id);
            }
        }
    }
}