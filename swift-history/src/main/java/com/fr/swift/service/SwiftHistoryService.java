package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.bean.SwiftTablePathBean;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.global.PushSegLocationRpcEvent;
import com.fr.swift.event.history.CheckLoadHistoryEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.manager.SwiftRepositoryManager;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentHelper;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.bean.impl.SegmentLocationInfoImpl;
import com.fr.swift.segment.event.SyncSegmentLocationEvent;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.task.service.ServiceTaskExecutor;
import com.fr.swift.util.FileUtil;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author pony
 * @date 2017/10/10
 */
@SwiftService(name = "history")
@ProxyService(HistoryService.class)
@SwiftBean(name = "history")
public class SwiftHistoryService extends AbstractSwiftService implements HistoryService, Serializable {
    private static final long serialVersionUID = -6013675740141588108L;

    private transient SwiftSegmentManager segmentManager;

    private transient ServiceTaskExecutor taskExecutor;

    private transient SwiftTablePathService tablePathService;

    private transient SwiftSegmentService segmentService;

    private transient ExecutorService loadDataService;

    private transient SwiftCubePathService cubePathService;

    private transient ClusterEventListener historyClusterListener;

    public SwiftHistoryService() {
        historyClusterListener = new HistoryClusterListener();
    }

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        segmentManager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        taskExecutor = SwiftContext.get().getBean(ServiceTaskExecutor.class);
        tablePathService = SwiftContext.get().getBean(SwiftTablePathService.class);
        segmentService = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);
        loadDataService = SwiftExecutors.newSingleThreadExecutor(new PoolThreadFactory(SwiftHistoryService.class));
        cubePathService = SwiftContext.get().getBean(SwiftCubePathService.class);
        final Map<SourceKey, Set<String>> needLoad = SegmentHelper.checkSegmentExists(segmentService, segmentManager);
        loadDataService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    SegmentHelper.download(needLoad, false);
                } catch (Exception e) {
                    SwiftLoggers.getLogger().warn(e);
                }
            }
        });
        ClusterListenerHandler.addExtraListener(historyClusterListener);
        return true;
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        super.shutdown();
        segmentManager = null;
        taskExecutor = null;
        tablePathService = null;
        loadDataService.shutdown();
        loadDataService = null;
        segmentService = null;
        ClusterListenerHandler.removeExtraListener(historyClusterListener);
        return true;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.HISTORY;
    }

    @Override
    public void removeHistory(List<SegmentKey> needRemoveList) {
        for (SegmentKey segmentKey : needRemoveList) {
            if (segmentKey.getStoreType().isPersistent()) {
                SegmentUtils.clearSegment(segmentKey);
            }
        }
    }

    @Override
    public void truncate(SourceKey tableKey) {
        SwiftTablePathBean entity = tablePathService.get(tableKey.getId());
        int path = 0;
        if (null != entity) {
            path = entity.getTablePath() == null ? 0 : entity.getTablePath();
            tablePathService.removePath(tableKey.getId());
        }
        Map<SourceKey, List<SegmentKey>> ownSegs = segmentService.getOwnSegments();
        // 删配置
        segmentService.removeSegments(tableKey.getId());
        // 同步seg location
        if (ownSegs.containsKey(tableKey)) {
            SwiftEventDispatcher.fire(SyncSegmentLocationEvent.REMOVE_SEG, ownSegs.get(tableKey));
        }

        SwiftMetaData metaData = SwiftContext.get().getBean(SwiftMetaDataService.class).getMetaDataByKey(tableKey.getId());
        // 删本地
        String localPath = new CubePathBuilder()
                .asAbsolute()
                .setSwiftSchema(metaData.getSwiftDatabase())
                .setTempDir(path)
                .setTableKey(tableKey).build();
        FileUtil.delete(localPath);
        // 删远程
        String remotePath = new CubePathBuilder().setSwiftSchema(metaData.getSwiftDatabase()).setTableKey(tableKey).build();
        try {
            SwiftRepositoryManager.getManager().currentRepo().delete(remotePath);
        } catch (IOException e) {
            SwiftLoggers.getLogger().warn("truncate remote data failed", e);
        }
    }

    /**
     * 加入集群后，historyService做集群相应处理
     */
    private class HistoryClusterListener implements ClusterEventListener, Serializable {

        private static final long serialVersionUID = 2365092184237741176L;

        @Override
        public void handleEvent(ClusterEvent clusterEvent) {
            if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
                RemoteSender senderProxy = ProxySelector.getInstance().getFactory().getProxy(RemoteSender.class);
                checkSegmentExists();
                sendLocalSegmentInfo(senderProxy);
                checkLoad(senderProxy);
            }
        }

        private void checkSegmentExists() {
//            SwiftClusterSegmentService segmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);
            final Map<SourceKey, Set<String>> needDownload = SegmentHelper.checkSegmentExists(segmentService, segmentManager);
            if (!needDownload.isEmpty()) {
                loadDataService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SegmentHelper.download(needDownload, false);
                        } catch (Exception e) {
                            SwiftLoggers.getLogger().warn(e);
                        }
                    }
                });
            }
        }

        private void sendLocalSegmentInfo(RemoteSender senderProxy) {
            SegmentLocationInfo info = loadSelfSegmentDestination();
            if (null != info) {
                try {
                    senderProxy.trigger(new PushSegLocationRpcEvent(info));
                } catch (Exception e) {
                    SwiftLoggers.getLogger().warn("Cannot sync native segment info to server! ", e);
                }
            }
        }

        private void checkLoad(RemoteSender senderProxy) {
            try {
                senderProxy.trigger(new CheckLoadHistoryEvent(getId()));
            } catch (Exception e) {
                SwiftLoggers.getLogger().warn("Cannot sync native segment info to server! ", e);
            }
        }

        protected SegmentLocationInfo loadSelfSegmentDestination() {
            Map<SourceKey, List<SegmentKey>> segments = segmentService.getOwnSegments();
            if (!segments.isEmpty()) {
                Map<SourceKey, List<SegmentDestination>> hist = new HashMap<SourceKey, List<SegmentDestination>>();
                for (Map.Entry<SourceKey, List<SegmentKey>> entry : segments.entrySet()) {
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
            return new SegmentDestinationImpl(clusterId, segmentKey.toString(), segmentKey.getOrder());
        }
    }
}
