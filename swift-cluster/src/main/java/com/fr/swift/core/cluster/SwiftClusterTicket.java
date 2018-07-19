package com.fr.swift.core.cluster;

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
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.event.ClusterType;

/**
 * This class created on 2018/5/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftClusterTicket extends ClusterTicketAdaptor {
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
//        Invoker invoker = clusterToolKit.getInvokerFactory().create(SwiftClusterService.getInstance());
//        ClusterNode masterNode = ClusterBridge.getView().getNodeById("lucifer-cluster2");
//
//        Method method = null;
//        try {
//            method = SwiftClusterService.class.getMethod("rpcSend", String.class, Object.class);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//        Invocation invocation = Invocation.create(method, "lucifer", "test" + System.currentTimeMillis());
//        Result result = invoker.invoke(masterNode, invocation);

        //注册rpc proxy
//        clusterServiceProxy = clusterToolKit.getRPCProxyFactory().newBuilder(SwiftClusterService.getInstance()).build();
//        FRProxyCache.registerProxy(ClusterService.class, clusterServiceProxy);
        //注册单例类型
//        FRProxyCache.registerInstance(SwiftClusterService.class, SwiftClusterService.getInstance());

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
