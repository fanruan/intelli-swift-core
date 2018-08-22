package com.fr.swift.boot;

import com.fr.cluster.core.ClusterNode;
import com.fr.cluster.core.event.ClusterViewEvent;
import com.fr.cluster.entry.ClusterTicketAdaptor;
import com.fr.cluster.entry.ClusterToolKit;
import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.general.ComparatorUtils;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.cluster.service.MasterService;
import com.fr.swift.cluster.service.SlaveService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.core.cluster.FRClusterNodeManager;
import com.fr.swift.core.cluster.FRClusterNodeService;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.event.ClusterType;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.nm.SlaveManager;
import com.fr.swift.nm.service.SwiftSlaveService;
import com.fr.swift.rm.MasterManager;
import com.fr.swift.rm.service.SwiftMasterService;
import com.fr.swift.service.listener.RemoteServiceSender;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.util.Crasher;

/**
 * This class created on 2018/5/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftClusterTicket extends ClusterTicketAdaptor {

    private static final SwiftClusterTicket INSTANCE = new SwiftClusterTicket();

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger();

    private SwiftServiceListenerHandler remoteServiceSender;

    private MasterManager masterManager;

    private SlaveManager slaveManager;

    private SwiftClusterTicket() {
    }

    public static SwiftClusterTicket getInstance() {
        return INSTANCE;
    }

    @Override
    public void beforeJoin() {
        masterManager = SwiftContext.get().getBean(MasterManager.class);
        slaveManager = SwiftContext.get().getBean(SlaveManager.class);
    }

    @Override
    public void approach(ClusterToolKit clusterToolKit) {
        //注册rpc服务
        remoteServiceSender = clusterToolKit.getRPCProxyFactory().newBuilder(RemoteServiceSender.getInstance()).build();

        EventDispatcher.listen(ClusterViewEvent.NODE_JOINED, new Listener<ClusterNode>() {
            @Override
            public void on(Event event, ClusterNode clusterNode) {
                LOGGER.info(String.format("%s join cluster!Master is %s", clusterNode.getID(), FRClusterNodeManager.getInstance().getMasterId()));
                if (FRClusterNodeManager.getInstance().isMaster()) {
                    ClusterSwiftServerService.getInstance().online(clusterNode.getID());
                }
            }
        });
        EventDispatcher.listen(ClusterViewEvent.NODE_LEFT, new Listener<ClusterNode>() {
            @Override
            public void on(Event event, ClusterNode clusterNode) {
                LOGGER.info(String.format("%s left cluster!Master is %s", clusterNode.getID(), FRClusterNodeManager.getInstance().getMasterId()));
                if (FRClusterNodeManager.getInstance().getMasterId() == null || ComparatorUtils.equals(FRClusterNodeManager.getInstance().getMasterId(), clusterNode.getID())) {
                    FRClusterNodeService.getInstance().competeMaster(clusterNode);
                    //只有重新选举master时候，才需要重新部署manager
                    try {
                        if (ComparatorUtils.equals(FRClusterNodeManager.getInstance().getMasterId(), FRClusterNodeManager.getInstance().getCurrentId())) {
                            slaveManager.shutDown();
                            masterManager.startUp();
                            ClusterSwiftServerService.getInstance().initService();
                        }
                    } catch (Exception e) {
                        Crasher.crash(e);
                    }
                }
                if (FRClusterNodeManager.getInstance().isMaster()) {
                    ClusterSwiftServerService.getInstance().offline(clusterNode.getID());
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
        FRClusterNodeService.getInstance().competeMaster();
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
