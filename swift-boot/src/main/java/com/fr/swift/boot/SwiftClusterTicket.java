package com.fr.swift.boot;

import com.fr.cluster.core.ClusterNode;
import com.fr.cluster.core.event.ClusterViewEvent;
import com.fr.cluster.engine.ticket.FineClusterToolKit;
import com.fr.cluster.entry.ClusterTicketAdaptor;
import com.fr.cluster.entry.ClusterToolKit;
import com.fr.cluster.rpc.base.ClusterInvoker;
import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.general.ComparatorUtils;
import com.fr.swift.cluster.service.ClusterAnalyseServiceImpl;
import com.fr.swift.cluster.service.ClusterHistoryServiceImpl;
import com.fr.swift.cluster.service.ClusterIndexingServiceImpl;
import com.fr.swift.cluster.service.ClusterRealTimeServiceImpl;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.cluster.service.SegmentLocationInfoContainer;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.core.cluster.FRClusterNodeManager;
import com.fr.swift.core.cluster.FRClusterNodeService;
import com.fr.swift.core.rpc.InvokerCache;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.event.ClusterType;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.nm.SlaveManager;
import com.fr.swift.rm.MasterManager;
import com.fr.swift.rm.view.NodeJoinedView;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.segment.impl.SegmentLocationInfoImpl;
import com.fr.swift.service.CollateService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.SwiftCollateService;
import com.fr.swift.service.cluster.ClusterAnalyseService;
import com.fr.swift.service.cluster.ClusterHistoryService;
import com.fr.swift.service.cluster.ClusterIndexingService;
import com.fr.swift.service.cluster.ClusterRealTimeService;
import com.fr.swift.service.listener.RemoteServiceSender;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.Crasher;

import java.util.List;
import java.util.Map;

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

    private static final long TIMEOUT = 30000L;

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
        ClusterInvoker remoteServiceSenderInvoker = FineClusterToolKit.getInstance().getInvokerFactory().create(SwiftContext.get().getBean(RemoteServiceSender.class), TIMEOUT);
        InvokerCache.getInstance().bindInvoker(RemoteServiceSender.class, remoteServiceSenderInvoker);
        InvokerCache.getInstance().bindInvoker(SwiftServiceListenerHandler.class, remoteServiceSenderInvoker);

        ClusterInvoker analyseServiceInvoker = FineClusterToolKit.getInstance().getInvokerFactory().create(SwiftContext.get().getBean(ClusterAnalyseService.class), TIMEOUT);
        InvokerCache.getInstance().bindInvoker(ClusterAnalyseServiceImpl.class, analyseServiceInvoker);
        InvokerCache.getInstance().bindInvoker(ClusterAnalyseService.class, analyseServiceInvoker);

        ClusterInvoker realTimeServiceInvoker = FineClusterToolKit.getInstance().getInvokerFactory().create(SwiftContext.get().getBean(ClusterRealTimeService.class), TIMEOUT);
        InvokerCache.getInstance().bindInvoker(ClusterRealTimeServiceImpl.class, realTimeServiceInvoker);
        InvokerCache.getInstance().bindInvoker(ClusterRealTimeService.class, realTimeServiceInvoker);

        ClusterInvoker indexingServiceInvoker = FineClusterToolKit.getInstance().getInvokerFactory().create(SwiftContext.get().getBean(ClusterIndexingService.class), TIMEOUT);
        InvokerCache.getInstance().bindInvoker(ClusterIndexingServiceImpl.class, indexingServiceInvoker);
        InvokerCache.getInstance().bindInvoker(ClusterIndexingService.class, indexingServiceInvoker);

        ClusterInvoker historyServiceInvoker = FineClusterToolKit.getInstance().getInvokerFactory().create(SwiftContext.get().getBean(ClusterHistoryService.class), TIMEOUT);
        InvokerCache.getInstance().bindInvoker(ClusterHistoryServiceImpl.class, historyServiceInvoker);
        InvokerCache.getInstance().bindInvoker(ClusterHistoryService.class, historyServiceInvoker);

        ClusterInvoker collateServiceInvoker = FineClusterToolKit.getInstance().getInvokerFactory().create(SwiftContext.get().getBean(CollateService.class), TIMEOUT);
        InvokerCache.getInstance().bindInvoker(CollateService.class, collateServiceInvoker);
        InvokerCache.getInstance().bindInvoker(SwiftCollateService.class, collateServiceInvoker);

        EventDispatcher.listen(ClusterViewEvent.NODE_JOINED, new Listener<ClusterNode>() {
            @Override
            public void on(Event event, ClusterNode clusterNode) {
                LOGGER.info(String.format("%s join cluster!Master is %s", clusterNode.getID(), FRClusterNodeManager.getInstance().getMasterId()));
                NodeJoinedView.getInstance().nodeJoin(clusterNode.getID());
                if (FRClusterNodeManager.getInstance().isMaster()) {
                    ClusterSwiftServerService.getInstance().online(clusterNode.getID());
                }
            }
        });
        EventDispatcher.listen(ClusterViewEvent.NODE_LEFT, new Listener<ClusterNode>() {
            @Override
            public void on(Event event, ClusterNode clusterNode) {
                LOGGER.info(String.format("%s left cluster!Master is %s", clusterNode.getID(), FRClusterNodeManager.getInstance().getMasterId()));
                NodeJoinedView.getInstance().nodeLeft(clusterNode.getID());
                if (FRClusterNodeManager.getInstance().getMasterId() == null || ComparatorUtils.equals(FRClusterNodeManager.getInstance().getMasterId(), clusterNode.getID())) {
                    FRClusterNodeService.getInstance().competeMaster(clusterNode);
                    //只有重新选举master时候，才需要重新部署manager
                    try {
                        if (ComparatorUtils.equals(FRClusterNodeManager.getInstance().getMasterId(), FRClusterNodeManager.getInstance().getCurrentId())) {
                            slaveManager.shutDown();
                            SegmentLocationInfoContainer.getContainer().clean();
                            SegmentLocationProvider.getInstance().removeTable(clusterNode.getID(), null);
                            Map<String, List<SegmentDestination>> hist = SegmentLocationProvider.getInstance().getSegmentInfo(ServiceType.HISTORY);
                            Map<String, List<SegmentDestination>> real = SegmentLocationProvider.getInstance().getSegmentInfo(ServiceType.REAL_TIME);
                            SegmentLocationInfo histInfo = new SegmentLocationInfoImpl(ServiceType.HISTORY, hist);
                            SegmentLocationInfo realInfo = new SegmentLocationInfoImpl(ServiceType.REAL_TIME, real);
                            SegmentLocationInfoContainer.getContainer().add(Pair.of(SegmentLocationInfo.UpdateType.ALL, histInfo));
                            SegmentLocationInfoContainer.getContainer().add(Pair.of(SegmentLocationInfo.UpdateType.ALL, realInfo));
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
                SegmentLocationProvider.getInstance().removeTable(clusterNode.getID(), null);
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
