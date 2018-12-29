package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.global.PushSegLocationRpcEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.query.session.factory.SessionFactory;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.bean.impl.SegmentLocationInfoImpl;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.impl.RealTimeSegDestImpl;
import com.fr.swift.segment.operator.Importer;
import com.fr.swift.segment.operator.delete.WhereDeleter;
import com.fr.swift.segment.recover.SegmentRecovery;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.alloter.impl.line.RealtimeLineSourceAlloter;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;
import com.fr.swift.util.IoUtil;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author pony
 * @date 2017/10/10
 */
@SwiftService(name = "realtime")
@ProxyService(RealtimeService.class)
@SwiftBean(name = "realtime")
public class SwiftRealtimeService extends AbstractSwiftService implements RealtimeService, Serializable {

    private static final long serialVersionUID = 4719723736240190155L;

    private transient SwiftSegmentManager segmentManager;

    private transient ServiceTaskExecutor taskExecutor;

    private transient volatile boolean recoverable = true;

    private transient ClusterEventListener realtimeClusterListener;

    public SwiftRealtimeService() {
        realtimeClusterListener = new RealtimeClusterListener();
    }

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        segmentManager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        taskExecutor = SwiftContext.get().getBean(ServiceTaskExecutor.class);
        if (recoverable) {
            recover0();
            recoverable = false;
        }
        ClusterListenerHandler.addExtraListener(realtimeClusterListener);
        return true;
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        super.shutdown();
        ResourceDiscovery.getInstance().releaseAll();
        recoverable = true;
        segmentManager = null;
        taskExecutor = null;
        ClusterListenerHandler.removeExtraListener(realtimeClusterListener);
        return true;
    }

    @Override
    public void insert(final SourceKey tableKey, final SwiftResultSet resultSet) throws Exception {
        taskExecutor.submit(new SwiftServiceCallable<Void>(tableKey, ServiceTaskType.INSERT, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    SwiftSourceAlloter alloter = new RealtimeLineSourceAlloter(tableKey, new LineAllotRule(LineAllotRule.MEM_STEP));
                    Table table = SwiftDatabase.getInstance().getTable(tableKey);
                    Importer importer = SwiftContext.get().getBean("incrementer", Importer.class, table, alloter);
                    importer.importData(resultSet);
                } finally {
                    IoUtil.close(resultSet);
                }
                return null;
            }
        }));
    }

    private void recover0() {
        for (Table table : SwiftDatabase.getInstance().getAllTables()) {
            final SourceKey tableKey = table.getSourceKey();
            try {
                taskExecutor.submit(new SwiftServiceCallable<Void>(tableKey, ServiceTaskType.RECOVERY, new Callable<Void>() {
                    @Override
                    public Void call() {
                        // 恢复所有realtime块
                        SegmentRecovery segmentRecovery = (SegmentRecovery) SwiftContext.get().getBean("segmentRecovery");
                        segmentRecovery.recover(tableKey);
                        return null;
                    }
                }));
            } catch (InterruptedException e) {
                SwiftLoggers.getLogger().warn(e);
            }
        }
    }

    @Override
    public void recover(List<SegmentKey> segKeys) {
        SwiftLoggers.getLogger().info("recover");
    }

    /**
     * todo SwiftHistoryService
     */
    @Override
    public SwiftResultSet query(final String queryDescription) throws SQLException {
        try {
            final QueryInfoBean bean = QueryBeanFactory.create(queryDescription);
            SessionFactory sessionFactory = SwiftContext.get().getBean(SessionFactory.class);
            // TODO: 2018/11/28  
            return (SwiftResultSet) sessionFactory.openSession(bean.getQueryId()).executeQuery(bean);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * todo 公用实现，SwiftHistoryService
     */
    @Override
    public boolean delete(final SourceKey sourceKey, final Where where, final List<String> needUpload) throws Exception {
        Future<Boolean> future = taskExecutor.submit(new SwiftServiceCallable<Boolean>(sourceKey, ServiceTaskType.DELETE, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
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
                return true;
            }
        }));
        return future.get();
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.REAL_TIME;
    }

    private class RealtimeClusterListener implements ClusterEventListener, Serializable {

        private static final long serialVersionUID = 7882776636815591790L;

        @Override
        public void handleEvent(ClusterEvent clusterEvent) {
            if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
                sendLocalSegmentInfo();
            }
        }

        private void sendLocalSegmentInfo() {
            SegmentLocationInfo info = loadSelfSegmentDestination();
            if (null != info) {
                RemoteSender senderProxy = ProxySelector.getInstance().getFactory().getProxy(RemoteSender.class);
                senderProxy.trigger(new PushSegLocationRpcEvent(info));
            }
        }

        protected SegmentLocationInfo loadSelfSegmentDestination() {
            SwiftClusterSegmentService clusterSegmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);
            Map<SourceKey, List<SegmentKey>> segments = clusterSegmentService.getOwnSegments();
            if (!segments.isEmpty()) {
                Map<SourceKey, List<SegmentDestination>> hist = new HashMap<SourceKey, List<SegmentDestination>>();
                for (Map.Entry<SourceKey, List<SegmentKey>> entry : segments.entrySet()) {
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

        private void initSegDestinations(Map<SourceKey, List<SegmentDestination>> map, SourceKey key) {
            if (null == map.get(key)) {
                map.put(key, new ArrayList<SegmentDestination>() {
                    @Override
                    public boolean add(SegmentDestination segmentDestination) {
                        return !contains(segmentDestination) && super.add(segmentDestination);
                    }
                });
            }
        }

        protected SegmentDestination createSegmentDestination(SegmentKey segmentKey) {
            String clusterId = ClusterSelector.getInstance().getFactory().getCurrentId();
            return new RealTimeSegDestImpl(clusterId, segmentKey.toString(), segmentKey.getOrder());
        }
    }
}