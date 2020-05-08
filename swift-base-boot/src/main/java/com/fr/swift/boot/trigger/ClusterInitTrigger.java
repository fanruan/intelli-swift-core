package com.fr.swift.boot.trigger;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.ClusterRegistry;
import com.fr.swift.cluster.base.service.ClusterBootService;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.cluster.base.initiator.MasterServiceInitiator;
import com.fr.swift.trigger.SwiftPriorityInitTrigger;

import java.util.List;
import java.util.Optional;

/**
 * This class created on 2020/4/28
 *
 * @author Kuifang.Liu
 */
public class ClusterInitTrigger implements SwiftPriorityInitTrigger {
    @Override
    public void trigger(Object data) throws Exception {
        if (SwiftProperty.get().isCluster()) {
            List<Class<?>> classesByAnnotations = SwiftContext.get().getClassesByAnnotations(ClusterRegistry.class);
            Optional<Class<?>> maxPriorityService = classesByAnnotations.stream().max((o1, o2) -> o2.getAnnotation(ClusterRegistry.class).priority() - o1.getAnnotation(ClusterRegistry.class).priority());
            ((ClusterBootService) SwiftContext.get().getBean(maxPriorityService.get())).init();

        } else {
            MasterServiceInitiator.getInstance().initByPriority(null);
        }
    }

    @Override
    public int priority() {
        return Priority.LOWER.priority();
    }
}
