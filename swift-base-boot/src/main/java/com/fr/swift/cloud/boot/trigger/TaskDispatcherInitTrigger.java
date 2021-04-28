package com.fr.swift.cloud.boot.trigger;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.executor.dispatcher.TaskDispatcher;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.service.executor.CollateExecutor;
import com.fr.swift.cloud.trigger.SwiftPriorityInitTrigger;

/**
 * @author lucifer
 * @date 2020/4/23
 * @description
 * @since swift 1.1
 */
public class TaskDispatcherInitTrigger implements SwiftPriorityInitTrigger {

    @Override
    public void init() {
        SwiftLoggers.getLogger().info("starting task dispatcher...");
        TaskDispatcher.getInstance();
        SwiftLoggers.getLogger().info("starting collate executor...");
        SwiftContext.get().getBean(CollateExecutor.class).start();
    }

    @Override
    public void destroy() throws InterruptedException {
        SwiftLoggers.getLogger().info("stopping task dispatcher...");
        TaskDispatcher.getInstance().stop();
        SwiftLoggers.getLogger().info("stopping collate executor...");
        SwiftContext.get().getBean(CollateExecutor.class).stop();
    }

    @Override
    public int priority() {
        return Priority.HIGH.priority();
    }
}
