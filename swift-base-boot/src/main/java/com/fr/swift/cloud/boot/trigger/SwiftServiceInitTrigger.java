package com.fr.swift.cloud.boot.trigger;

import com.fineio.FineIO;
import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.service.ServiceContext;
import com.fr.swift.cloud.trigger.SwiftPriorityInitTrigger;
import com.fr.swift.cloud.util.concurrent.SwiftExecutors;

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
