package com.fr.swift.cloud.boot.trigger;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.dao.NodeInfoService;
import com.fr.swift.cloud.listener.SwiftMigrateRetryListener;
import com.fr.swift.cloud.listener.SwiftTriggerMigrateListener;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.service.TaskService;
import com.fr.swift.cloud.trigger.SwiftPriorityInitTrigger;
import com.fr.swift.cloud.util.concurrent.PoolThreadFactory;
import com.fr.swift.cloud.util.concurrent.SwiftExecutors;

import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Heng.J
 * @date 2020/10/23
 * @description
 * @since swift-1.2.0
 */
public class TaskDistributeTrigger implements SwiftPriorityInitTrigger {

    private ScheduledExecutorService executorService;

    /**
     * 启动或竞争成主节点后 1min 分发计划任务
     */
    @Override
    public void init() {
        if (executorService == null) {
            SwiftLoggers.getLogger().info("starting task distribute...");
            SwiftMigrateRetryListener.listen();
            SwiftTriggerMigrateListener.listen();
            executorService = SwiftExecutors.newScheduledThreadPool(1, new PoolThreadFactory("SwiftPlaningTaskPool"));
            executorService.schedule(productTask(), 60, TimeUnit.SECONDS);
        }
    }

    private Runnable productTask() {
        return () -> {
            try {
                NodeInfoService nodeInfoService = SwiftContext.get().getBean(NodeInfoService.class);
                TaskService taskService = SwiftContext.get().getBean(TaskService.class);

                // migrate tasks
                Set<String> nodeIds = nodeInfoService.getMigrateNodeIds();
                nodeIds.forEach(nodeId ->
                        nodeInfoService.getMigrateInfosById(nodeId)
                                .stream()
                                .findFirst()
                                .ifPresent(taskInfo -> taskService.distributeTask(taskInfo, nodeId)));

                // other tasks
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        };
    }

    @Override
    public void destroy() {
        if (executorService != null) {
            SwiftLoggers.getLogger().info("stopping task distribute...");
            SwiftMigrateRetryListener.remove();
            SwiftTriggerMigrateListener.remove();
            executorService.shutdown();
            executorService = null;
        }
    }

    @Override
    public int priority() {
        return Priority.LOW.priority();
    }
}
