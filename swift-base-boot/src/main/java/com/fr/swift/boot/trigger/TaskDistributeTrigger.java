package com.fr.swift.boot.trigger;

import com.fr.swift.SwiftContext;
import com.fr.swift.dao.NodeInfoService;
import com.fr.swift.listener.SwiftMigrateRetryListener;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.TaskService;
import com.fr.swift.trigger.SwiftPriorityInitTrigger;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

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

    private final ScheduledExecutorService executorService = SwiftExecutors.newScheduledThreadPool(1, new PoolThreadFactory("SwiftPlaningTaskPool"));

    /**
     * 启动或竞争成主节点后 1min 分发计划任务
     */
    @Override
    public void init() {
        SwiftLoggers.getLogger().info("starting task distribute...");
        SwiftMigrateRetryListener.listen();
        executorService.schedule(productTask(), 60, TimeUnit.SECONDS);
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
        SwiftLoggers.getLogger().info("stopping task distribute...");
        SwiftMigrateRetryListener.remove();
        executorService.shutdown();
    }

    @Override
    public int priority() {
        return Priority.LOW.priority();
    }
}
