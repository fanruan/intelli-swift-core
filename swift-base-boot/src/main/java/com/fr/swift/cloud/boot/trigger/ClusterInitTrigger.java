package com.fr.swift.cloud.boot.trigger;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.annotation.ClusterRegistry;
import com.fr.swift.cloud.basics.base.JdkProxyFactory;
import com.fr.swift.cloud.basics.base.selector.ProxySelector;
import com.fr.swift.cloud.basics.base.selector.UrlSelector;
import com.fr.swift.cloud.cluster.base.selector.ClusterNodeSelector;
import com.fr.swift.cloud.cluster.base.service.ClusterBootService;
import com.fr.swift.cloud.cluster.node.impl.SwiftClusterNodeManagerImpl;
import com.fr.swift.cloud.netty.rpc.invoke.RPCInvokerCreator;
import com.fr.swift.cloud.netty.rpc.url.RPCUrlFactory;
import com.fr.swift.cloud.property.SwiftProperty;
import com.fr.swift.cloud.trigger.SwiftPriorityInitTrigger;

import java.util.List;
import java.util.Optional;

/**
 * This class created on 2020/4/28
 *
 * @author Kuifang.Liu
 */
public class ClusterInitTrigger implements SwiftPriorityInitTrigger {

    @Override
    public void init() {
        if (SwiftProperty.get().isCluster()) {
            ProxySelector.getInstance().switchFactory(new JdkProxyFactory(new RPCInvokerCreator()));
            UrlSelector.getInstance().switchFactory(new RPCUrlFactory());
            // TODO: 2020/5/8 可以用个更好的实现，现在先这样吧
            ClusterNodeSelector.getInstance().switchFactory(new SwiftClusterNodeManagerImpl());

            List<Class<?>> classesByAnnotations = SwiftContext.get().getClassesByAnnotations(ClusterRegistry.class);
            Optional<Class<?>> maxPriorityService = classesByAnnotations.stream()
                    .max((o1, o2) -> o2.getAnnotation(ClusterRegistry.class).priority() - o1.getAnnotation(ClusterRegistry.class).priority());
            ((ClusterBootService) SwiftContext.get().getBean(maxPriorityService.get())).init();
        }
    }

    @Override
    public void destroy() {
        if (SwiftProperty.get().isCluster()) {
            List<Class<?>> classesByAnnotations = SwiftContext.get().getClassesByAnnotations(ClusterRegistry.class);
            Optional<Class<?>> maxPriorityService = classesByAnnotations.stream()
                    .max((o1, o2) -> o2.getAnnotation(ClusterRegistry.class).priority() - o1.getAnnotation(ClusterRegistry.class).priority());
            ((ClusterBootService) SwiftContext.get().getBean(maxPriorityService.get())).destroy();
        }
    }

    @Override
    public int priority() {
        return Priority.MEDIAN.priority();
    }
}
