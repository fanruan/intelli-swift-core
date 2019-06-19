package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.global.PushSegLocationRpcEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.executor.TaskProducer;
import com.fr.swift.executor.task.impl.RecoveryExecutorTask;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Incrementer;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.bean.impl.SegmentLocationInfoImpl;
import com.fr.swift.segment.column.impl.base.ResourceDiscovery;
import com.fr.swift.segment.event.SyncSegmentLocationEvent;
import com.fr.swift.segment.impl.RealTimeSegDestImpl;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.BaseAllotRule;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.alloter.impl.line.RealtimeLineSourceAlloter;
import com.fr.swift.util.FileUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pony
 * @date 2017/10/10
 */
@SwiftService(name = "realtime")
@SwiftBean(name = "realtime")
public class SwiftRealtimeService extends AbstractSwiftService implements RealtimeService, Serializable {

    private static final long serialVersionUID = 4719723736240190155L;

    private transient volatile boolean recoverable = true;

    private transient ClusterEventListener realtimeClusterListener;

    private transient SwiftSegmentService segSvc;

    public SwiftRealtimeService() {
        realtimeClusterListener = new RealtimeClusterListener();
    }

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        segSvc = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);
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
        segSvc = null;
        ClusterListenerHandler.removeExtraListener(realtimeClusterListener);
        return true;
    }

    @Override
    public void insert(final SourceKey tableKey, final SwiftResultSet resultSet) throws Exception {
        SwiftSourceAlloter alloter = new RealtimeLineSourceAlloter(tableKey, new LineAllotRule(BaseAllotRule.MEM_CAPACITY));
        Table table = SwiftDatabase.getInstance().getTable(tableKey);
        new Incrementer<SwiftSourceAlloter<?, RowInfo>>(table, alloter).importData(resultSet);
    }

    private void recover0() {
        for (Table table : SwiftDatabase.getInstance().getAllTables()) {
            SourceKey tableKey = table.getSourceKey();
            Map<SourceKey, List<SegmentKey>> ownSegKeys = segSvc.getOwnSegments();

            if (!ownSegKeys.containsKey(tableKey)) {
                continue;
            }
            for (SegmentKey segKey : ownSegKeys.get(tableKey)) {
                if (segKey.getStoreType().isPersistent()) {
                    continue;
                }
                try {
                    TaskProducer.produceTask(new RecoveryExecutorTask(segKey));
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        }
    }

    @Override
    public void truncate(SourceKey tableKey) {
        Map<SourceKey, List<SegmentKey>> ownSegs = segSvc.getOwnSegments();
        // 删配置
        segSvc.removeSegments(tableKey.getId());
        if (ownSegs.containsKey(tableKey)) {
            // 同步seg location
            SwiftEventDispatcher.fire(SyncSegmentLocationEvent.REMOVE_SEG, ownSegs.get(tableKey));
        }

        SwiftMetaData meta = SwiftContext.get().getBean(SwiftMetaDataService.class).getMetaDataByKey(tableKey.getId());
        // 删bak
        String bakPath = new CubePathBuilder()
                .asAbsolute()
                .asBackup()
                .setSwiftSchema(meta.getSwiftSchema())
                .setTableKey(tableKey).build();
        FileUtil.delete(bakPath);
        // 删内存
        ResourceDiscovery.getInstance().releaseTable(meta.getSwiftSchema(), tableKey);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.REAL_TIME;
    }

    private class RealtimeClusterListener implements ClusterEventListener, Serializable {

        private static final long serialVersionUID = 7882776636815591790L;

        @Override
        public void handleEvent(ClusterEvent clusterEvent) {
//            if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
//                ResourceDiscovery.getInstance().releaseAll();
//                recover0();
//                sendLocalSegmentInfo();
//            }
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