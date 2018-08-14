package com.fr.swift.boot;

import com.fr.cluster.core.ClusterNode;
import com.fr.cluster.core.event.ClusterViewEvent;
import com.fr.cluster.entry.ClusterTicketAdaptor;
import com.fr.cluster.entry.ClusterToolKit;
import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.general.ComparatorUtils;
import com.fr.swift.core.cluster.FRClusterNodeManager;
import com.fr.swift.core.cluster.SwiftClusterService;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.event.ClusterType;
import com.fr.swift.service.listener.RemoteServiceSender;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;

/**
 * This class created on 2018/5/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftClusterTicket extends ClusterTicketAdaptor {
    private static final SwiftClusterTicket INSTANCE = new SwiftClusterTicket();

    private SwiftServiceListenerHandler remoteServiceSender = null;

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
        //注册rpc服务
        remoteServiceSender = clusterToolKit.getRPCProxyFactory().newBuilder(RemoteServiceSender.getInstance()).build();

        EventDispatcher.listen(ClusterViewEvent.NODE_LEFT, new Listener<ClusterNode>() {
            @Override
            public void on(Event event, ClusterNode clusterNode) {
                if (FRClusterNodeManager.getInstance().getMasterId() == null || ComparatorUtils.equals(FRClusterNodeManager.getInstance().getMasterId(), clusterNode.getID())) {
                    SwiftClusterService.getInstance().competeMaster();
                }
            }
        });
    }

    @Override
    public void catchUpWith(ClusterNode clusterNode) {
    }

    /**
     * 加入集群后，向master注册集群service
     */
    @Override
    public void afterJoin() {
        SwiftClusterService.getInstance().competeMaster();
        ClusterListenerHandler.handlerEvent(new ClusterEvent(ClusterEventType.JOIN_CLUSTER, ClusterType.FR));
        FRClusterNodeManager.getInstance().setCluster(true);
    }

    /**
     * 离开集群后，取消本地的集群模式
     */
    @Override
    public void onLeft() {
        ClusterListenerHandler.handlerEvent(new ClusterEvent(ClusterEventType.LEFT_CLUSTER, ClusterType.FR));
        FRClusterNodeManager.getInstance().setCluster(false);
    }
}
