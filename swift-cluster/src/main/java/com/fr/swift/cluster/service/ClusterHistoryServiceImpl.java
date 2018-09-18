package com.fr.swift.cluster.service;

import com.fr.event.EventDispatcher;
import com.fr.swift.annotation.RpcMethod;
import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.config.entity.SwiftTablePathEntity;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.Where;
import com.fr.swift.event.global.PushSegLocationRpcEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.SwiftRepositoryManager;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.segment.impl.SegmentLocationInfoImpl;
import com.fr.swift.segment.operator.delete.WhereDeleter;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.AbstractSwiftService;
import com.fr.swift.service.HistoryService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.cluster.ClusterHistoryService;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.task.service.ServiceTaskType;
import com.fr.swift.task.service.SwiftServiceCallable;
import com.fr.swift.util.FileUtil;
import com.fr.swift.util.ServiceBeanFactory;
import com.fr.swift.utils.ClusterCommonUtils;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.beans.factory.annotation.Qualifier;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/8/7
 */
@SwiftService(name = "history", cluster = true)
@RpcService(value = ClusterHistoryService.class, type = RpcService.RpcServiceType.INTERNAL)
@Service("clusterHistoryService")
public class ClusterHistoryServiceImpl extends AbstractSwiftService implements ClusterHistoryService, Serializable {
    private static final long serialVersionUID = -3487010910076432934L;

    @Autowired(required = false)
    @Qualifier("historyService")
    private HistoryService historyService;
    private transient ServiceTaskExecutor taskExecutor;
    private transient SwiftSegmentManager segmentManager;
    private transient SwiftRepositoryManager repositoryManager;
    private transient SwiftTablePathService tablePathService;
    private transient SwiftCubePathService cubePathService;

    @Override
    public boolean start() throws SwiftServiceException {
        List<com.fr.swift.service.SwiftService> services = ServiceBeanFactory.getSwiftServiceByNames(Collections.singleton("history"));
        historyService = (HistoryService) services.get(0);
        historyService.setId(getID());
        historyService.start();
        segmentManager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        taskExecutor = SwiftContext.get().getBean(ServiceTaskExecutor.class);
        repositoryManager = SwiftContext.get().getBean(SwiftRepositoryManager.class);
        tablePathService = SwiftContext.get().getBean(SwiftTablePathService.class);
        cubePathService = SwiftContext.get().getBean(SwiftCubePathService.class);
        SegmentLocationInfo info = loadSelfSegmentDestination();
        if (null != info) {
            try {
                ClusterCommonUtils.asyncCallMaster(new PushSegLocationRpcEvent(info));
            } catch (Exception e) {
                SwiftLoggers.getLogger().warn("Cannot sync native segment info to server! ", e);
            }
        }
        return super.start();
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        historyService = null;
        segmentManager = null;
        taskExecutor = null;
        repositoryManager = null;
        return super.shutdown();
    }

    @Override
    @RpcMethod(methodName = "historyQuery")
    public SwiftResultSet query(String queryInfo) throws Exception {
        return historyService.query(queryInfo);
    }

    @Override
    @RpcMethod(methodName = "load")
    public void load(Map<String, Set<String>> remoteUris, boolean replace) throws Exception {
        historyService.load(remoteUris, replace);
    }

    @Override
    public boolean delete(SourceKey sourceKey, Where where) throws Exception {
        return historyService.delete(sourceKey, where);
    }


    protected SegmentDestination createSegmentDestination(SegmentKey segmentKey) {
        String clusterId = ClusterSelector.getInstance().getFactory().getCurrentId();
        return new SegmentDestinationImpl(clusterId, segmentKey.toString(), segmentKey.getOrder(), ClusterHistoryService.class, "historyQuery");
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
                    if (segmentKey.getStoreType() == Types.StoreType.FINE_IO) {
                        hist.get(entry.getKey()).add(createSegmentDestination(segmentKey));
                    }
                }
            }
            if (hist.isEmpty()) {
                return null;
            }
            return new SegmentLocationInfoImpl(ServiceType.HISTORY, hist);
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

    @Override
    @RpcMethod(methodName = "historyDelete")
    public boolean delete(final SourceKey sourceKey, final Where where, final List<String> needUpload) throws Exception {
        taskExecutor.submit(new SwiftServiceCallable(sourceKey, ServiceTaskType.DELETE) {
            @Override
            public void doJob() throws Exception {
                List<SegmentKey> segmentKeys = segmentManager.getSegmentKeys(sourceKey);
                for (SegmentKey segKey : segmentKeys) {
                    if (segKey.getStoreType() != Types.StoreType.FINE_IO) {
                        continue;
                    }
                    WhereDeleter whereDeleter = (WhereDeleter) SwiftContext.get().getBean("decrementer", segKey);
                    ImmutableBitMap allShowBitmap = whereDeleter.delete(where);
                    if (needUpload.contains(segKey.toString())) {
                        if (allShowBitmap.isEmpty()) {
                            EventDispatcher.fire(SegmentEvent.UNLOAD_HISTORY, segKey);
                        } else {
                            EventDispatcher.fire(SegmentEvent.MASK_HISTORY, segKey);
                        }
                    }
                }
            }
        });
        return true;
    }

    @Override
    @RpcMethod(methodName = "truncate")
    public void truncate(String sourceKey) {
        SwiftTablePathEntity entity = tablePathService.get(sourceKey);
        int path = 0;
        if (null != entity) {
            path = entity.getTablePath() == null ? 0 : entity.getTablePath();
            tablePathService.removePath(sourceKey);
        }
        SwiftSegmentService segmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);
        segmentService.removeSegments(sourceKey);

        String localPath = String.format("%s/%d/%s", cubePathService.getSwiftPath(), path, sourceKey);
        FileUtil.delete(localPath);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.HISTORY;
    }
}
