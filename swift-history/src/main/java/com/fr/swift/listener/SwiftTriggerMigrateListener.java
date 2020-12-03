package com.fr.swift.listener;

import com.fr.swift.SwiftContext;
import com.fr.swift.dao.NodeInfoService;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.executor.task.info.MigTriggerInfo;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.TaskService;
import com.fr.swift.service.event.NodeEvent;
import com.fr.swift.service.event.NodeMessage;

import java.util.List;

/**
 * @author Heng.J
 * @date 2020/12/3
 * @description
 * @since swift-1.2.0
 */
public class SwiftTriggerMigrateListener implements SwiftEventListener<NodeMessage> {

    private static final SwiftTriggerMigrateListener LISTENER = new SwiftTriggerMigrateListener();

    private final NodeInfoService nodeInfoService = SwiftContext.get().getBean(NodeInfoService.class);

    private final TaskService taskService = SwiftContext.get().getBean(TaskService.class);

    public static void listen() {
        SwiftEventDispatcher.listen(NodeEvent.START_WAITING, LISTENER);
    }

    public static void remove() {
        SwiftEventDispatcher.remove(LISTENER);
    }

    @Override
    public void on(NodeMessage nodeMessage) {
        String clusterId = nodeMessage.getClusterId();
        nodeInfoService.updateReadyStatusById(clusterId);
        //H.J TODO : 2020/12/3 还是存在小概率风险
        List<String> clusterIds = nodeInfoService.getIdsByBlockIndex(nodeInfoService.getBlockIndexById(clusterId));
        int count = clusterIds.stream().mapToInt(nodeInfoService::getReadyStatusById).sum();
        if (count == clusterIds.size()) {
            for (String id : clusterIds) {
                SwiftLoggers.getLogger().info("Start trigger {} 's migrate job", id);
                taskService.distributeTask(new MigTriggerInfo(), id);
            }
        }
    }
}