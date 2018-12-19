package com.fr.swift.cluster.service;

import com.fr.swift.annotation.RpcMethod;
import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cluster.listener.NodeStartedListener;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.global.PushSegLocationRpcEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.SwiftRepositoryManager;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.impl.RealTimeSegDestImpl;
import com.fr.swift.segment.impl.SegmentLocationInfoImpl;
import com.fr.swift.segment.operator.delete.WhereDeleter;
import com.fr.swift.segment.recover.SegmentRecovery;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.AbstractSwiftService;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.cluster.ClusterRealTimeService;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;
import com.fr.swift.util.ServiceBeanFactory;
import com.fr.swift.utils.ClusterCommonUtils;
import com.fr.third.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/8/6
 */
@SwiftService(name = "realtime", cluster = true)
@RpcService(type = RpcService.RpcServiceType.INTERNAL, value = ClusterRealTimeService.class)
public class ClusterRealTimeServiceImpl extends AbstractSwiftService implements ClusterRealTimeService, Serializable {
    private static final long serialVersionUID = 946204307880678794L;

    @Autowired
    private transient ServiceTaskExecutor taskExecutor;

    @Autowired(required = false)
    private RealtimeService realtimeService;

    private transient HashSet<SourceKey> existsTableKey = new HashSet<SourceKey>();

    private transient SwiftSegmentManager segmentManager;
    private transient SwiftRepositoryManager repositoryManager;

    @Override
    public boolean start() throws SwiftServiceException {
        List<com.fr.swift.service.SwiftService> services = ServiceBeanFactory.getSwiftServiceByNames(Collections.singleton("realtime"));
        realtimeService = (RealtimeService) services.get(0);
        realtimeService.setId(getID());
        realtimeService.start();
        repositoryManager = SwiftContext.get().getBean(SwiftRepositoryManager.class);
        segmentManager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        NodeStartedListener.INSTANCE.registerTask(new NodeStartedListener.NodeStartedTask() {
            @Override
            public void run() {
                switchToCluster();
                recover0();
                sendLocalSegmentInfo();
            }
        });
        return super.start();
    }

    private void recover0() {
        for (Table table : SwiftDatabase.getInstance().getAllTables()) {
            final SourceKey tableKey = table.getSourceKey();
            try {
                taskExecutor.submit(new SwiftServiceCallable(tableKey, ServiceTaskType.RECOVERY) {
                    @Override
                    public void doJob() {
                        // 恢复所有realtime块
                        SegmentRecovery segmentRecovery = (SegmentRecovery) SwiftContext.get().getBean("segmentRecovery");
                        segmentRecovery.recover(tableKey);
                    }
                });
            } catch (InterruptedException e) {
                SwiftLoggers.getLogger().warn(e);
            }
        }
    }

    private void sendLocalSegmentInfo() {
        SegmentLocationInfo info = loadSelfSegmentDestination();
        if (null != info) {
            rpcSegmentLocation(new PushSegLocationRpcEvent(info));
        }
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        realtimeService = null;
        repositoryManager = null;
        segmentManager = null;
        return super.shutdown();
    }

    @Override
    @RpcMethod(methodName = "realtimneInsert")
    public void insert(final SourceKey tableKey, final SwiftResultSet resultSet) throws Exception {

        taskExecutor.submit(new SwiftServiceCallable(tableKey, ServiceTaskType.INSERT) {
            @Override
            public void doJob() throws Exception {
                if (!existsTableKey.contains(tableKey)) {
                    existsTableKey.add(tableKey);
                    rpcSegmentLocation(new PushSegLocationRpcEvent(makeLocationInfo(tableKey)));
                }
                SwiftDatabase.getInstance().getTable(tableKey).insert(resultSet);
            }
        });
    }

    @Override
    public boolean delete(SourceKey tableKey, Where where) throws Exception {
        return realtimeService.delete(tableKey, where);
    }

    @Override
    @RpcMethod(methodName = "recover")
    public void recover(List<SegmentKey> segKeys) throws Exception {
        realtimeService.recover(segKeys);
    }

    @Override
    @RpcMethod(methodName = "realTimeQuery")
    public SwiftResultSet query(String queryInfo) throws Exception {
        return realtimeService.query(queryInfo);
    }

    private void rpcSegmentLocation(PushSegLocationRpcEvent event) {
        try {
            ClusterCommonUtils.runSyncMaster(event);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    protected SegmentDestination createSegmentDestination(SegmentKey segmentKey) {
        String clusterId = ClusterSelector.getInstance().getFactory().getCurrentId();
        return new RealTimeSegDestImpl(clusterId, segmentKey.toString(), segmentKey.getOrder(), ClusterRealTimeService.class, "realTimeQuery");
    }

    protected SegmentDestination createSegmentDestination(SourceKey segmentKey) {
        String clusterId = ClusterSelector.getInstance().getFactory().getCurrentId();
        return new RealTimeSegDestImpl(clusterId, String.format("%s@%s@%d", segmentKey.getId(), Types.StoreType.MEMORY.name(), -1), -1, ClusterRealTimeService.class, "realTimeQuery");
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.REAL_TIME;
    }

    private void switchToCluster() {
        SwiftClusterSegmentService clusterSegmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);
        clusterSegmentService.setClusterId(getID());
        SwiftSegmentLocationService locationService = SwiftContext.get().getBean(SwiftSegmentLocationService.class);
        Map<String, List<SegmentKey>> segments = clusterSegmentService.getOwnSegments("LOCAL");
        if (!segments.isEmpty()) {
            for (Map.Entry<String, List<SegmentKey>> entry : segments.entrySet()) {
                for (SegmentKey segmentKey : entry.getValue()) {
                    if (segmentKey.getStoreType().isTransient()) {
                        locationService.updateClusterId(segmentKey.toString(), "LOCAL", getID());
                    }
                }
            }
        }
    }

    protected SegmentLocationInfo loadSelfSegmentDestination() {
        SwiftClusterSegmentService clusterSegmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);
        clusterSegmentService.setClusterId(getID());
        Map<String, List<SegmentKey>> segments = clusterSegmentService.getOwnSegments();
        if (!segments.isEmpty()) {
            Map<String, List<SegmentDestination>> hist = new HashMap<String, List<SegmentDestination>>();
            for (Map.Entry<String, List<SegmentKey>> entry : segments.entrySet()) {
                initSegDestinations(hist, entry.getKey());
                for (SegmentKey segmentKey : entry.getValue()) {
                    if (segmentKey.getStoreType().isTransient()) {
                        hist.get(entry.getKey()).add(createSegmentDestination(segmentKey));
                    }
                }
            }
            if (hist.isEmpty()) {
                return null;
            }
            return new SegmentLocationInfoImpl(ServiceType.REAL_TIME, hist);
        }
        return null;
    }

    private void initSegDestinations(Map<String, List<SegmentDestination>> map, String key) {
        if (null == map.get(key)) {
            map.put(key, new ArrayList<SegmentDestination>() {
                @Override
                public boolean add(SegmentDestination segmentDestination) {
                    return !contains(segmentDestination) && super.add(segmentDestination);
                }
            });
        }
    }

    protected SegmentLocationInfo makeLocationInfo(SourceKey sourceKey) {
        Map<String, List<SegmentDestination>> map = new HashMap<String, List<SegmentDestination>>();
        List<SegmentDestination> list = Collections.singletonList(createSegmentDestination(sourceKey));
        map.put(sourceKey.getId(), list);
        return new SegmentLocationInfoImpl(ServiceType.REAL_TIME, map);
    }

    @Override
    @RpcMethod(methodName = "realtimeDelete")
    public boolean delete(final SourceKey sourceKey, final Where where, final List<String> needUpload) throws Exception {
        taskExecutor.submit(new SwiftServiceCallable(sourceKey, ServiceTaskType.DELETE) {
            @Override
            public void doJob() throws Exception {
                List<SegmentKey> segmentKeys = segmentManager.getSegmentKeys(sourceKey);
                for (SegmentKey segKey : segmentKeys) {
                    if (!segmentManager.existsSegment(segKey)) {
                        continue;
                    }
                    WhereDeleter whereDeleter = (WhereDeleter) SwiftContext.get().getBean("decrementer", segKey);
                    ImmutableBitMap allShowBitmap = whereDeleter.delete(where);
                    if (segKey.getStoreType().isTransient()) {
                        continue;
                    }

                    if (needUpload.contains(segKey.toString())) {
                        if (allShowBitmap.isEmpty()) {
                            SwiftEventDispatcher.fire(SegmentEvent.REMOVE_HISTORY, segKey);
                        } else {
                            SwiftEventDispatcher.fire(SegmentEvent.MASK_HISTORY, segKey);
                        }
                    }
                }
            }
        });
        return true;
    }
}
