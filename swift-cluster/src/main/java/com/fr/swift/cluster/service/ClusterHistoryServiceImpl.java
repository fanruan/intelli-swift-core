package com.fr.swift.cluster.service;

import com.fr.swift.annotation.RpcMethod;
import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cluster.listener.NodeStartedListener;
import com.fr.swift.config.entity.SwiftTablePathEntity;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.db.Where;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.global.PushSegLocationRpcEvent;
import com.fr.swift.event.history.CheckLoadHistoryEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.SwiftRepository;
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
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;
import com.fr.swift.utils.ClusterCommonUtils;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.beans.factory.annotation.Qualifier;
import com.fr.third.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

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
    private transient ExecutorService loadDataService;


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
        loadDataService = SwiftExecutors.newSingleThreadExecutor(new PoolThreadFactory(ClusterHistoryServiceImpl.class));
        NodeStartedListener.INSTANCE.registerTask(new NodeStartedListener.NodeStartedTask() {
            @Override
            public void run() {
                checkSegmentExists();
                switchToCluster();
                sendLocalSegmentInfo();
                checkLoad();
            }
        });
        return super.start();
    }

    private void switchToCluster() {
        SwiftClusterSegmentService clusterSegmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);
        clusterSegmentService.setClusterId(getID());
        SwiftSegmentLocationService locationService = SwiftContext.get().getBean(SwiftSegmentLocationService.class);
        Map<String, List<SegmentKey>> segments = clusterSegmentService.getOwnSegments("LOCAL");
        if (!segments.isEmpty()) {
            SwiftRepository repository = repositoryManager.currentRepo();
            for (Map.Entry<String, List<SegmentKey>> entry : segments.entrySet()) {
                for (SegmentKey segmentKey : entry.getValue()) {
                    if (segmentKey.getStoreType().isPersistent()) {
                        locationService.updateClusterId(segmentKey.toString(), "LOCAL", getID());
                        String remotePath = String.format("%s/%s", segmentKey.getSwiftSchema().getDir(), segmentKey.getUri().getPath());
                        String cubePath = CubeUtil.getSegPath(segmentKey);
                        try {
                            repository.copyToRemote(cubePath, remotePath);
                        } catch (IOException e) {
                            SwiftLoggers.getLogger().warn("upload cause an exception", e);
                        }
                    }
                }
            }
        }
    }

    private void checkLoad() {
        try {
            ClusterCommonUtils.runSyncMaster(new CheckLoadHistoryEvent(getID()));
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("Cannot sync native segment info to server! ", e);
        }
    }

    private void checkSegmentExists() {
        SwiftRepository repository = repositoryManager.currentRepo();
        SwiftClusterSegmentService segmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);
        segmentService.setClusterId(getID());
        Map<String, List<SegmentKey>> map = segmentService.getOwnSegments();
        for (Map.Entry<String, List<SegmentKey>> entry : map.entrySet()) {
            String table = entry.getKey();
            List<SegmentKey> value = entry.getValue();
            List<SegmentKey> notExists = new ArrayList<SegmentKey>();
            final Map<String, Set<String>> needDownload = new HashMap<String, Set<String>>();
            for (SegmentKey segmentKey : value) {
                if (segmentKey.getStoreType().isPersistent()) {
                    if (!segmentManager.getSegment(segmentKey).isReadable()) {
                        String downloadPath = segmentKey.getUri().getPath();
                        String remotePath = String.format("%s/%s", segmentKey.getSwiftSchema().getDir(), downloadPath);
                        if (repository.exists(remotePath)) {
                            if (null == needDownload.get(table)) {
                                needDownload.put(table, new HashSet<String>());
                            }
                            needDownload.get(table).add(downloadPath);
                        } else {
                            notExists.add(segmentKey);
                        }
                    }
                }
            }
            if (!needDownload.isEmpty()) {
                loadDataService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            load(needDownload, false);
                        } catch (Exception e) {
                            SwiftLoggers.getLogger().error(e);
                        }
                    }
                });
            }
            segmentService.removeSegments(notExists);
        }
    }

    private void sendLocalSegmentInfo() {
        SegmentLocationInfo info = loadSelfSegmentDestination();
        if (null != info) {
            try {
                ClusterCommonUtils.runSyncMaster(new PushSegLocationRpcEvent(info));
            } catch (Exception e) {
                SwiftLoggers.getLogger().warn("Cannot sync native segment info to server! ", e);
            }
        }
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        historyService = null;
        segmentManager = null;
        taskExecutor = null;
        repositoryManager = null;
        loadDataService.shutdown();
        loadDataService = null;
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
    @RpcMethod(methodName = "removeHistory")
    public void removeHistory(List<SegmentKey> needRemoveList) {
        historyService.removeHistory(needRemoveList);
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
                    if (segmentKey.getStoreType().isPersistent()) {
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
                    if (segKey.getStoreType().isTransient()) {
                        continue;
                    }
                    if (!segmentManager.existsSegment(segKey)) {
                        continue;
                    }
                    WhereDeleter whereDeleter = (WhereDeleter) SwiftContext.get().getBean("decrementer", segKey);
                    ImmutableBitMap allShowBitmap = whereDeleter.delete(where);
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
