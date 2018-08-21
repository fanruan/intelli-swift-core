package com.fr.swift.server;

import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.local.LocalProxyFactory;
import com.fr.swift.local.LocalUrlFactory;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.proxy.RPCProxyFactory;
import com.fr.swift.netty.rpc.url.RpcUrlFactory;
import com.fr.swift.node.SwiftClusterNodeManager;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.SwiftRegister;
import com.fr.swift.service.register.ClusterSwiftRegister;
import com.fr.swift.service.register.LocalSwiftRegister;

/**
 * This class created on 2018/7/18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftClusterListener implements ClusterEventListener {

    @Override
    public void handleEvent(ClusterEvent clusterEvent) {
        try {
            if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
                ProxySelector.getInstance().switchFactory(new RPCProxyFactory());
                UrlSelector.getInstance().switchFactory(new RpcUrlFactory());
                ClusterSelector.getInstance().switchFactory(SwiftClusterNodeManager.getInstance());

                SwiftContext.get().getBean("localSwiftRegister", LocalSwiftRegister.class).serviceUnregister();
                SwiftContext.get().getBean("clusterSwiftRegister", SwiftRegister.class).serviceRegister();
            } else if (clusterEvent.getEventType() == ClusterEventType.LEFT_CLUSTER) {
                ProxySelector.getInstance().switchFactory(new LocalProxyFactory());
                UrlSelector.getInstance().switchFactory(new LocalUrlFactory());

                SwiftContext.get().getBean("clusterSwiftRegister", ClusterSwiftRegister.class).serviceUnregister();
                SwiftContext.get().getBean("localSwiftRegister", LocalSwiftRegister.class).serviceRegister();
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}
