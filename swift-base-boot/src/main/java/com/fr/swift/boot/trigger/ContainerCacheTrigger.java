package com.fr.swift.boot.trigger;

import com.fr.swift.SwiftContext;
import com.fr.swift.dao.NodeInfoService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.trigger.SwiftPriorityInitTrigger;

/**
 * @author Heng.J
 * @date 2020/12/1
 * @description for master cache
 * @since swift-1.2.0
 */
public class ContainerCacheTrigger implements SwiftPriorityInitTrigger {

    private NodeInfoService nodeInfoService;

    @Override
    public void init() throws Exception {
        if (nodeInfoService == null) {
            SwiftLoggers.getLogger().info("starting init master cache...");
            nodeInfoService = SwiftContext.get().getBean(NodeInfoService.class);
            nodeInfoService.flushCache();
        }
    }

    @Override
    public void destroy() throws Exception {
        if (nodeInfoService != null) {
            SwiftLoggers.getLogger().info("starting clear master cache...");
            nodeInfoService.clearCache();
            nodeInfoService = null;
        }
    }

    @Override
    public int priority() {
        return Priority.MEDIAN.priority();
    }
}
