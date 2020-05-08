package com.fr.swift.boot.trigger;

import com.fr.swift.SwiftContext;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.ServiceContext;
import com.fr.swift.trigger.SwiftPriorityInitTrigger;

/**
 * @author lucifer
 * @date 2020/4/23
 * @description
 * @since swift 1.1
 */
public class SwiftServiceInitTrigger implements SwiftPriorityInitTrigger {

    @Override
    public void trigger(Object data) throws Exception {
        SwiftLoggers.getLogger().info("starting swift services...");
        SwiftContext.get().getBean(ServiceContext.class).start();
    }

    @Override
    public int priority() {
        return Priority.HIGHER.priority();
    }
}
