package com.fr.swift.listener;

import com.fr.swift.SwiftContext;
import com.fr.swift.dao.NodeInfoService;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.TaskService;
import com.fr.swift.service.event.NodeEvent;

/**
 * @author Heng.J
 * @date 2020/11/13
 * @description
 * @since swift-1.2.0
 */
public class SwiftMigrateRetryListener implements SwiftEventListener<String> {

    private static final SwiftMigrateRetryListener LISTENER = new SwiftMigrateRetryListener();

    private final NodeInfoService nodeInfoService = SwiftContext.get().getBean(NodeInfoService.class);

    private final TaskService taskService = SwiftContext.get().getBean(TaskService.class);

    public static void listen() {
        SwiftEventDispatcher.listen(NodeEvent.RETRY_DISTRIBUTE, LISTENER);
    }

    public static void remove() {
        SwiftEventDispatcher.remove(LISTENER);
    }

    @Override
    public void on(String clusterId) {
        SwiftLoggers.getLogger().info("Start update or retry distribute migrateTask", clusterId);
        nodeInfoService.getMigrateInfosById(clusterId).stream().findFirst().ifPresent(taskInfo -> taskService.distributeTask(taskInfo, clusterId));
    }
}