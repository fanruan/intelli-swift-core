package com.fr.swift.boot.trigger;

import com.fr.swift.SwiftContext;
import com.fr.swift.executor.dispatcher.TaskDispatcher;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.executor.CollateExecutor;
import com.fr.swift.trigger.SwiftPriorityInitTrigger;

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
    }

    @Override
    public int priority() {
        return Priority.LOW.priority();
    }
}
