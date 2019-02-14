package com.fr.swift.boot;

import com.fr.cluster.core.ClusterNode;
import com.fr.cluster.core.event.ClusterViewEvent;
import com.fr.cluster.entry.ClusterTicketAdaptor;
import com.fr.cluster.entry.ClusterToolKit;
import com.fr.cluster.rpc.base.ClusterInvoker;
import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.general.ComparatorUtils;
import com.fr.swift.ClusterNodeService;
import com.fr.swift.SwiftContext;
import com.fr.swift.boot.synchronizer.MasterSynchronizer;
import com.fr.swift.cluster.core.cluster.FRClusterNodeManager;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.cluster.service.SegmentLocationInfoContainer;
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
import com.fr.swift.segment.bean.impl.SegmentLocationInfoImpl;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.service.CollateService;
import com.fr.swift.service.DeleteService;
import com.fr.swift.service.HistoryService;
import com.fr.swift.service.IndexingService;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.SwiftAnalyseService;
import com.fr.swift.service.SwiftCollateService;
import com.fr.swift.service.SwiftDeleteService;
import com.fr.swift.service.SwiftHistoryService;
import com.fr.swift.service.SwiftIndexingService;
import com.fr.swift.service.SwiftRealtimeService;
import com.fr.swift.service.SwiftUploadService;
import com.fr.swift.service.UploadService;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.service.listener.RemoteServiceSender;
import com.fr.swift.source.SourceKey;
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
        ClusterInvoker remoteServiceSenderInvoker = clusterToolKit.getInvokerFactory().create(SwiftContext.get().getBean(RemoteServiceSender.class), TIMEOUT);
        InvokerCache.getInstance().bindInvoker(RemoteServiceSender.class, remoteServiceSenderInvoker);
        InvokerCache.getInstance().bindInvoker(RemoteSender.class, remoteServiceSenderInvoker);

        ClusterInvoker analyseServiceInvoker = clusterToolKit.getInvokerFactory().create(SwiftContext.get().getBean(AnalyseService.class), TIMEOUT);
        InvokerCache.getInstance().bindInvoker(SwiftAnalyseService.class, analyseServiceInvoker);
        InvokerCache.getInstance().bindInvoker(AnalyseService.class, analyseServiceInvoker);

        ClusterInvoker realTimeServiceInvoker = clusterToolKit.getInvokerFactory().create(SwiftContext.get().getBean(RealtimeService.class), TIMEOUT);
        InvokerCache.getInstance().bindInvoker(SwiftRealtimeService.class, realTimeServiceInvoker);
        InvokerCache.getInstance().bindInvoker(RealtimeService.class, realTimeServiceInvoker);

        ClusterInvoker indexingServiceInvoker = clusterToolKit.getInvokerFactory().create(SwiftContext.get().getBean(IndexingService.class), TIMEOUT);
        InvokerCache.getInstance().bindInvoker(SwiftIndexingService.class, indexingServiceInvoker);
        InvokerCache.getInstance().bindInvoker(IndexingService.class, indexingServiceInvoker);

        ClusterInvoker historyServiceInvoker = clusterToolKit.getInvokerFactory().create(SwiftContext.get().getBean(HistoryService.class), TIMEOUT);
        InvokerCache.getInstance().bindInvoker(SwiftHistoryService.class, historyServiceInvoker);
        InvokerCache.getInstance().bindInvoker(HistoryService.class, historyServiceInvoker);

        ClusterInvoker collateServiceInvoker = clusterToolKit.getInvokerFactory().create(SwiftContext.get().getBean(CollateService.class), TIMEOUT);
        InvokerCache.getInstance().bindInvoker(CollateService.class, collateServiceInvoker);
        InvokerCache.getInstance().bindInvoker(SwiftCollateService.class, collateServiceInvoker);

        ClusterInvoker deleteServiceInvoker = clusterToolKit.getInvokerFactory().create(SwiftContext.get().getBean(DeleteService.class), TIMEOUT);
        InvokerCache.getInstance().bindInvoker(DeleteService.class, deleteServiceInvoker);
        InvokerCache.getInstance().bindInvoker(SwiftDeleteService.class, deleteServiceInvoker);

        ClusterInvoker uploadServiceInvoker = clusterToolKit.getInvokerFactory().create(SwiftContext.get().getBean(UploadService.class), TIMEOUT);
        InvokerCache.getInstance().bindInvoker(UploadService.class, uploadServiceInvoker);
        InvokerCache.getInstance().bindInvoker(SwiftUploadService.class, uploadServiceInvoker);

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
                    SwiftContext.get().getBean(ClusterNodeService.class).competeMaster(clusterNode);
                    //只有重新选举master时候，才需要重新部署manager
                    try {
                        if (ComparatorUtils.equals(FRClusterNodeManager.getInstance().getMasterId(), FRClusterNodeManager.getInstance().getCurrentId())) {
                            slaveManager.shutDown();
                            SegmentLocationInfoContainer.getContainer().clean();
                            SegmentLocationProvider.getInstance().removeTable(clusterNode.getID(), null);
                            Map<SourceKey, List<SegmentDestination>> hist = SegmentLocationProvider.getInstance().getSegmentInfo(ServiceType.HISTORY);
                            Map<SourceKey, List<SegmentDestination>> real = SegmentLocationProvider.getInstance().getSegmentInfo(ServiceType.REAL_TIME);
                            SegmentLocationInfo histInfo = new SegmentLocationInfoImpl(ServiceType.HISTORY, hist);
                            SegmentLocationInfo realInfo = new SegmentLocationInfoImpl(ServiceType.REAL_TIME, real);
                            SegmentLocationInfoContainer.getContainer().add(Pair.of(SegmentLocationInfo.UpdateType.ALL, histInfo));
                            SegmentLocationInfoContainer.getContainer().add(Pair.of(SegmentLocationInfo.UpdateType.ALL, realInfo));
                            masterManager.startUp();
                            MasterSynchronizer.getInstance().start();
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
        SwiftContext.get().getBean(ClusterNodeService.class).competeMaster();
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
