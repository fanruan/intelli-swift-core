package com.fr.swift.frrpc;

import com.fr.cluster.core.ClusterNode;
import com.fr.cluster.core.event.ClusterViewEvent;
import com.fr.cluster.entry.ClusterTicketAdaptor;
import com.fr.cluster.entry.ClusterToolKit;
import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.general.ComparatorUtils;
import com.fr.swift.ClusterService;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.proxy.LocalProxyFactory;
import com.fr.swift.selector.ProxySelector;

/**
 * This class created on 2018/5/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftClusterTicket extends ClusterTicketAdaptor {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftClusterTicket.class);

    private static final SwiftClusterTicket INSTANCE = new SwiftClusterTicket();

    private ClusterService clusterServiceProxy = null;

    private SwiftClusterTicket() {
    }

    public static SwiftClusterTicket getInstance() {
        return INSTANCE;
    }

    @Override
    public void beforeJoin() {

    }

    @Override
    public void approach(ClusterToolKit clusterToolKit) {
        //注册rpc proxy
        clusterServiceProxy = clusterToolKit.getRPCProxyFactory().newBuilder(SwiftClusterService.getInstance()).build();
        FRProxyCache.registerProxy(ClusterService.class, clusterServiceProxy);

        SwiftClusterService.getInstance().competeMaster();

        EventDispatcher.listen(ClusterViewEvent.NODE_LEFT, new Listener<ClusterNode>() {
            @Override
            public void on(Event event, ClusterNode clusterNode) {
                if (ClusterNodeManager.getInstance().getMasterId() == null || ComparatorUtils.equals(ClusterNodeManager.getInstance().getMasterId(), clusterNode.getID())) {
                    SwiftClusterService.getInstance().competeMaster();
                }
            }
        });

        ClusterListenerHandler.addListener(new ClusterEventListener() {
            @Override
            public void handleEvent(ClusterEvent clusterEvent) {
                if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
                    ProxySelector.getInstance().switchFactory(new FRClusterProxyFactory());
                } else if (clusterEvent.getEventType() == ClusterEventType.LEFT_CLUSTER) {
                    ProxySelector.getInstance().switchFactory(new LocalProxyFactory());
                }
            }
        });
    }

    @Override
    public void catchUpWith(ClusterNode clusterNode) {

    }

    @Override
    public void afterJoin() {
        ClusterListenerHandler.handlerEvent(new ClusterEvent(ClusterEventType.JOIN_CLUSTER));
        ClusterNodeManager.getInstance().setCluster(true);
    }

    @Override
    public void onLeft() {
        ClusterListenerHandler.handlerEvent(new ClusterEvent(ClusterEventType.LEFT_CLUSTER));
        ClusterNodeManager.getInstance().setCluster(false);
    }
}
