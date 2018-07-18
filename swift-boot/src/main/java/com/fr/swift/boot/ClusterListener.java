package com.fr.swift.boot;

import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftSegmentServiceProvider;
import com.fr.swift.config.service.impl.SwiftClusterSegmentServiceImpl;
import com.fr.swift.configure.SwiftClusterNodeManager;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.core.rpc.FRClusterNodeManager;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterType;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.proxy.LocalProxyFactory;
import com.fr.swift.rpc.proxy.RPCProxyFactory;
import com.fr.swift.rpc.url.RpcUrlFactory;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.selector.UrlSelector;
import com.fr.swift.service.register.ClusterSwiftRegister;
import com.fr.swift.service.register.LocalSwiftRegister;
import com.fr.swift.url.LocalUrlFactory;

/**
 * This class created on 2018/6/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ClusterListener implements ClusterEventListener {
    public void handleEvent(ClusterEvent clusterEvent) {
        if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
            ProxySelector.getInstance().switchFactory(new RPCProxyFactory());
            UrlSelector.getInstance().switchFactory(new RpcUrlFactory());

            if (clusterEvent.getClusterType() == ClusterType.CONFIGURE) {
                ClusterSelector.getInstance().switchFactory(SwiftClusterNodeManager.getInstance());
            } else {
                ClusterSelector.getInstance().switchFactory(FRClusterNodeManager.getInstance());
            }

            new LocalSwiftRegister().serviceUnregister();
            ((ClusterSwiftRegister) SwiftContext.get().getBean("clusterSwiftRegister")).serviceRegister();
            SwiftClusterSegmentServiceImpl service = (SwiftClusterSegmentServiceImpl) SwiftContext.get().getBean(SwiftClusterSegmentService.class);
            service.setClusterId(ClusterSelector.getInstance().getFactory().getCurrentId());
            SwiftSegmentServiceProvider.getProvider().setService(service);
        } else if (clusterEvent.getEventType() == ClusterEventType.LEFT_CLUSTER) {
            try {
                ProxySelector.getInstance().switchFactory(new LocalProxyFactory());
                UrlSelector.getInstance().switchFactory(new LocalUrlFactory());

                ((ClusterSwiftRegister) SwiftContext.get().getBean("clusterSwiftRegister")).serviceUnregister();
                new LocalSwiftRegister().serviceRegister();
                SwiftSegmentServiceProvider.getProvider().setService(SwiftContext.get().getBean("swiftSegmentService", SwiftSegmentService.class));
            } catch (SwiftServiceException e) {
                e.printStackTrace();
            }
        }
    }
}
