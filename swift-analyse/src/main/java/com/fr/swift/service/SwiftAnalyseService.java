package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.event.analyse.RequestSegLocationEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.session.factory.SessionFactory;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.bean.impl.SegmentLocationInfoImpl;
import com.fr.swift.segment.container.SegmentContainer;
import com.fr.swift.segment.impl.RealTimeSegDestImpl;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pony
 * @date 2017/10/12
 * 分析服务
 */
@SwiftService(name = "analyse")
@ProxyService(AnalyseService.class)
@SwiftBean(name = "analyse")
public class SwiftAnalyseService extends AbstractSwiftService implements AnalyseService, Serializable {
    private static final long serialVersionUID = 841582089735823794L;

    private transient SessionFactory sessionFactory;
    private transient boolean loadable = true;

    private transient ClusterEventListener analyseClusterListener;

    public SwiftAnalyseService() {
        analyseClusterListener = new AnalyseClusterListener();
    }


    @Override
    public boolean start() throws SwiftServiceException {
        boolean start = super.start();
        sessionFactory = SwiftContext.get().getBean("swiftQuerySessionFactory", SessionFactory.class);
        cacheSegments();
        ClusterListenerHandler.addExtraListener(analyseClusterListener);
        return start;
    }

    private void cacheSegments() {
        SwiftClusterSegmentService clusterSegmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);
        Map<SourceKey, List<SegmentKey>> segments = clusterSegmentService.getOwnSegments();
        SwiftSegmentManager manager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        if (!segments.isEmpty()) {
            for (Map.Entry<SourceKey, List<SegmentKey>> entry : segments.entrySet()) {
                for (SegmentKey segmentKey : entry.getValue()) {
                    manager.getSegment(segmentKey);
                }
            }
        }
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        super.shutdown();
        sessionFactory = null;
        ClusterListenerHandler.removeExtraListener(analyseClusterListener);
        loadable = true;
        return true;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.ANALYSE;
    }

    @Override
    public QueryResultSet getQueryResult(String queryJson) throws Exception {
        SwiftLoggers.getLogger().debug(queryJson);
        // TODO: 2018/12/12
        QueryBean info = QueryBeanFactory.create(queryJson);
        return sessionFactory.openSession(info.getQueryId()).executeQuery(info);
    }

    @Override
    public void updateSegmentInfo(SegmentLocationInfo locationInfo, SegmentLocationInfo.UpdateType updateType) {
        String clusterId = getId();
        for (List<SegmentDestination> value : locationInfo.getDestinations().values()) {
            for (SegmentDestination segmentDestination : value) {
                ((SegmentDestinationImpl) segmentDestination).setCurrentNode(clusterId);
            }
        }
        SegmentLocationProvider.getInstance().updateSegmentInfo(locationInfo, updateType);
    }

    @Override
    public void removeSegments(String clusterId, SourceKey sourceKey, List<String> segmentKeys) {
        SegmentLocationProvider.getInstance().removeSegments(clusterId, sourceKey, segmentKeys);
        SegmentContainer.NORMAL.remove(sourceKey);
    }

    private class AnalyseClusterListener implements ClusterEventListener, Serializable {

        private static final long serialVersionUID = 782230506049436206L;

        @Override
        public void handleEvent(ClusterEvent clusterEvent) {
            if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
                SwiftClusterSegmentService clusterSegmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);
                loadSegmentLocationInfo(clusterSegmentService);
            }
        }

        private void loadSegmentLocationInfo(SwiftClusterSegmentService clusterSegmentService) {
            if (loadable) {
                loadSelfSegmentDestination(clusterSegmentService);
                loadable = false;
            }
            RemoteSender senderProxy = ProxySelector.getInstance().getFactory().getProxy(RemoteSender.class);
            List<Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo>> result =
                    (List<Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo>>) senderProxy.trigger(new RequestSegLocationEvent(getId()));
            if (!result.isEmpty()) {
                for (Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo> pair : result) {
                    updateSegmentInfo(pair.getValue(), pair.getKey());
                }
            }
        }

        private void loadSelfSegmentDestination(SwiftClusterSegmentService clusterSegmentService) {
            Map<SourceKey, List<SegmentKey>> segments = clusterSegmentService.getOwnSegments();
            SwiftSegmentManager manager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
            if (!segments.isEmpty()) {
                Map<SourceKey, List<SegmentDestination>> hist = new HashMap<SourceKey, List<SegmentDestination>>();
                Map<SourceKey, List<SegmentDestination>> realTime = new HashMap<SourceKey, List<SegmentDestination>>();
                for (Map.Entry<SourceKey, List<SegmentKey>> entry : segments.entrySet()) {
                    initSegDestinations(hist, entry.getKey());
                    initSegDestinations(realTime, entry.getKey());
                    for (SegmentKey segmentKey : entry.getValue()) {
                        if (segmentKey.getStoreType().isPersistent()) {
                            hist.get(entry.getKey()).add(new SegmentDestinationImpl(getId(), segmentKey.toString(), segmentKey.getOrder()));
                        } else {
                            realTime.get(entry.getKey()).add(new RealTimeSegDestImpl(getId(), segmentKey.toString(), segmentKey.getOrder()));
                        }
                        manager.getSegment(segmentKey);
                    }
                }
                updateSegmentInfo(new SegmentLocationInfoImpl(ServiceType.HISTORY, hist), SegmentLocationInfo.UpdateType.PART);
                updateSegmentInfo(new SegmentLocationInfoImpl(ServiceType.REAL_TIME, realTime), SegmentLocationInfo.UpdateType.PART);
            }
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
    }
}
