package com.fr.swift.boot.trigger;

import com.fineio.FineIO;
import com.fr.swift.SwiftContext;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.ServiceContext;
import com.fr.swift.trigger.SwiftPriorityInitTrigger;
import com.fr.swift.util.concurrent.SwiftExecutors;

/**
 * @author lucifer
 * @date 2020/4/23
 * @description
 * @since swift 1.1
 */
public class SwiftServiceInitTrigger implements SwiftPriorityInitTrigger {

    @Override
    public void init() throws Exception {
        SwiftLoggers.getLogger().info("starting fineio...");
        FineIO.start();
        SwiftLoggers.getLogger().info("starting swift services...");
        SwiftContext.get().getBean(ServiceContext.class).start();
    }

    @Override
    public void destroy() throws Exception {
        SwiftLoggers.getLogger().info("stopping swift services...");
        SwiftContext.get().getBean(ServiceContext.class).shutdown();
        SwiftLoggers.getLogger().info("stopping swift executors...");
        SwiftExecutors.shutdownAllNow();
        FineIO.stop();
    }

    @Override
    public int priority() {
        return Priority.HIGHER.priority();
    }
}
