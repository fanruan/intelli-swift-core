package com.fr.swift.service;

import com.fr.swift.annotation.RpcMethod;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.analyse.RequestSegLocationEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.Queryable;
import com.fr.swift.query.info.bean.query.QueryInfoBeanFactory;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryBeanFactory;
import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.query.session.factory.SessionFactory;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.impl.RealTimeSegDestImpl;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.segment.impl.SegmentLocationInfoImpl;
import com.fr.swift.service.cluster.ClusterHistoryService;
import com.fr.swift.service.cluster.ClusterRealTimeService;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.Assert;
import com.fr.third.springframework.beans.factory.annotation.Autowired;

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
public class SwiftAnalyseService extends AbstractSwiftService implements AnalyseService {
    private static final long serialVersionUID = 841582089735823794L;

    private transient SessionFactory sessionFactory;
    private transient boolean loadable = true;
    @Autowired(required = false)
    private transient QueryBeanFactory queryBeanFactory;

    public SwiftAnalyseService() {
    }

    @Override
    public boolean start() throws SwiftServiceException {
        boolean start = super.start();
        QueryRunnerProvider.getInstance().registerRunner(this);
        this.sessionFactory = SwiftContext.get().getBean("swiftQuerySessionFactory", SessionFactory.class);
        cacheSegments();
        return start;
    }

    private void cacheSegments() {
        SwiftClusterSegmentService clusterSegmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);
        clusterSegmentService.setClusterId("LOCAL");
        Map<String, List<SegmentKey>> segments = clusterSegmentService.getOwnSegments();
        SwiftSegmentManager manager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        if (!segments.isEmpty()) {
            for (Map.Entry<String, List<SegmentKey>> entry : segments.entrySet()) {
                for (SegmentKey segmentKey : entry.getValue()) {
                    manager.getSegment(segmentKey);
                }
            }
        }
    }

    private void loadSegmentLocationInfo() {
        if (loadable) {
            loadSelfSegmentDestination();
            loadable = false;
        }
        List<Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo>> result =
                (List<Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo>>) ProxySelector.getInstance().getFactory().getProxy(RemoteSender.class).trigger(new RequestSegLocationEvent(getID()));
        if (!result.isEmpty()) {
            for (Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo> pair : result) {
                updateSegmentInfo(pair.getValue(), pair.getKey());
            }
        }
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

    private void loadSelfSegmentDestination() {
        SwiftClusterSegmentService clusterSegmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);
        clusterSegmentService.setClusterId(getID());
        Map<String, List<SegmentKey>> segments = clusterSegmentService.getOwnSegments();
        SwiftSegmentManager manager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        if (!segments.isEmpty()) {
            Map<String, List<SegmentDestination>> hist = new HashMap<String, List<SegmentDestination>>();
            Map<String, List<SegmentDestination>> realTime = new HashMap<String, List<SegmentDestination>>();
            for (Map.Entry<String, List<SegmentKey>> entry : segments.entrySet()) {
                initSegDestinations(hist, entry.getKey());
                initSegDestinations(realTime, entry.getKey());
                for (SegmentKey segmentKey : entry.getValue()) {
                    if (segmentKey.getStoreType().isPersistent()) {
                        hist.get(entry.getKey()).add(new SegmentDestinationImpl(getID(), segmentKey.toString(), segmentKey.getOrder(), ClusterHistoryService.class, "historyQuery"));
                    } else {
                        realTime.get(entry.getKey()).add(new RealTimeSegDestImpl(getID(), segmentKey.toString(), segmentKey.getOrder(), ClusterRealTimeService.class, "realTimeQuery"));
                    }
                    manager.getSegment(segmentKey);
                }
            }
            updateSegmentInfo(new SegmentLocationInfoImpl(ServiceType.HISTORY, hist), SegmentLocationInfo.UpdateType.PART);
            updateSegmentInfo(new SegmentLocationInfoImpl(ServiceType.REAL_TIME, realTime), SegmentLocationInfo.UpdateType.PART);
        }
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        super.shutdown();
        sessionFactory = null;
        QueryRunnerProvider.getInstance().registerRunner(null);
        return true;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.ANALYSE;
    }

    @Override
    public SwiftResultSet getQueryResult(QueryBean info) throws Exception {
        SwiftLoggers.getLogger().debug(QueryInfoBeanFactory.queryBean2String(info));
        return sessionFactory.openSession(info.getQueryId()).executeQuery(info);
    }

    @Override
    public SwiftResultSet getRemoteQueryResult(final String jsonString, final SegmentDestination remoteURI) {
        try {
            return queryRemoteNodeNode(jsonString, remoteURI);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Query remote node error! ", e);
            return null;
        }
    }

    private SwiftResultSet queryRemoteNodeNode(String jsonString, SegmentDestination remoteURI) throws Exception {
        if (null == remoteURI) {
            QueryBean queryBean = queryBeanFactory.create(jsonString, false);
            remoteURI = queryBean.getQueryDestination();
        }
        Assert.notNull(remoteURI);
        Class<? extends Queryable> clazz = remoteURI.getServiceClass();
        Queryable queryable = ProxySelector.getInstance().getFactory().getProxy(clazz);
        return queryable.query(jsonString);
    }

    @Override
    @RpcMethod(methodName = "updateSegmentInfo")
    public void updateSegmentInfo(SegmentLocationInfo locationInfo, SegmentLocationInfo.UpdateType updateType) {
        String clusterId = getID();
        for (List<SegmentDestination> value : locationInfo.getDestinations().values()) {
            for (SegmentDestination segmentDestination : value) {
                ((SegmentDestinationImpl) segmentDestination).setCurrentNode(clusterId);
            }
        }
        SegmentLocationProvider.getInstance().updateSegmentInfo(locationInfo, updateType);
    }

    @Override
    @RpcMethod(methodName = "removeTable")
    public void removeTable(String cluster, String sourceKey) {
        SegmentLocationProvider.getInstance().removeTable(cluster, sourceKey);
    }

    @Override
    @RpcMethod(methodName = "removeSegments")
    public void removeSegments(String clusterId, String sourceKey, List<String> segmentKeys) {
        SegmentLocationProvider.getInstance().removeSegments(clusterId, sourceKey, segmentKeys);
    }
}
