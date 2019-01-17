package com.fr.swift.cluster.service;

import com.fr.swift.annotation.RpcMethod;
import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.cluster.listener.NodeStartedListener;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.analyse.RequestSegLocationEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.netty.rpc.server.ServiceMethodRegistry;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryBeanFactory;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.container.SegmentContainer;
import com.fr.swift.segment.impl.RealTimeSegDestImpl;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.segment.impl.SegmentLocationInfoImpl;
import com.fr.swift.service.AbstractSwiftService;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.cluster.ClusterAnalyseService;
import com.fr.swift.service.cluster.ClusterHistoryService;
import com.fr.swift.service.cluster.ClusterRealTimeService;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.Assert;
import com.fr.swift.util.ServiceBeanFactory;
import com.fr.swift.utils.ClusterCommonUtils;
import com.fr.third.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author yee
 * @date 2018/8/6
 */
@SwiftService(name = "analyse", cluster = true)
@RpcService(value = ClusterAnalyseService.class, type = RpcService.RpcServiceType.INTERNAL)
public class ClusterAnalyseServiceImpl extends AbstractSwiftService implements ClusterAnalyseService {
    private static final long serialVersionUID = 7637989460502966453L;
    @Autowired(required = false)
    private transient QueryBeanFactory queryBeanFactory;
    @Autowired(required = false)
    private transient AnalyseService analyseService;
    private transient boolean loadable = true;
    private transient SwiftSegmentService segmentProvider;

    @Override
    public SwiftResultSet getRemoteQueryResult(final String jsonString, final SegmentDestination remoteURI) {
//        SwiftLoggers.getLogger().debug("query: " + jsonString + "\n" + "node: " + (null != remoteURI ? remoteURI.toString() : StringUtils.EMPTY));
        final SwiftResultSet[] resultSet = new SwiftResultSet[1];
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            queryRemoteNodeNode(jsonString, remoteURI).addCallback(new AsyncRpcCallback() {
                @Override
                public void success(Object result) {
                    try {
                        resultSet[0] = (SwiftResultSet) result;
                    } finally {
                        latch.countDown();
                    }
                }

                @Override
                public void fail(Exception e) {
                    List<String> spareNodes = remoteURI.getSpareNodes();
                    try {
                        for (String spareNode : spareNodes) {
                            SegmentDestination spare = remoteURI.copy();
                            spare.setClusterId(spareNode);
                            final CountDownLatch count = new CountDownLatch(1);
                            queryRemoteNodeNode(jsonString, spare).addCallback(new AsyncRpcCallback() {
                                @Override
                                public void success(Object result) {
                                    try {
                                        resultSet[0] = (SwiftResultSet) result;
                                    } finally {
                                        count.countDown();
                                    }
                                }

                                @Override
                                public void fail(Exception e) {
                                    count.countDown();
                                }
                            });
                            count.await();
                            if (null != resultSet[0]) {
                                latch.countDown();
                                break;
                            }
                        }
                        if (resultSet[0] == null) {
                            // 远程查询抛错这边应该知晓
                            // TODO 远程查询抛错应该怎么处理
                            latch.countDown();
                            SwiftLoggers.getLogger().error("Query remote node error! ", e);
                            SwiftLoggers.getLogger().error("caused by query: {}\n", jsonString);
                        }
                    } catch (Exception e1) {
                        SwiftLoggers.getLogger().error("Query remote node error! ", e1);
                        SwiftLoggers.getLogger().error("caused by query: {}\n", jsonString);
                    }
                }
            });
            latch.await();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Query remote node error! ", e);
            SwiftLoggers.getLogger().error("caused by query: {}\n", jsonString);
        }
        return resultSet[0];
    }

    @Override
    public boolean start() throws SwiftServiceException {
        List<com.fr.swift.service.SwiftService> services = ServiceBeanFactory.getSwiftServiceByNames(Collections.singleton("analyse"));
        analyseService = (AnalyseService) services.get(0);
        analyseService.setId(getID());
        analyseService.start();
        segmentProvider = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);
        // 这边为了覆盖掉analyse的注册，所以再调一次注册
        super.start();
        final SwiftClusterSegmentService clusterSegmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);
        clusterSegmentService.setClusterId(getID());
        NodeStartedListener.INSTANCE.registerTask(new NodeStartedListener.NodeStartedTask() {
            @Override
            public void run() {
                loadSegmentLocationInfo(clusterSegmentService);
            }
        });
        return true;
    }

    private void loadSegmentLocationInfo(SwiftClusterSegmentService clusterSegmentService) {
        if (loadable) {
            loadSelfSegmentDestination(clusterSegmentService);
            loadable = false;
        }
        List<Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo>> result =
                (List<Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo>>) ClusterCommonUtils.runSyncMaster(new RequestSegLocationEvent(getID()));
        if (!result.isEmpty()) {
            for (Pair<SegmentLocationInfo.UpdateType, SegmentLocationInfo> pair : result) {
                updateSegmentInfo(pair.getValue(), pair.getKey());
            }
        }
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        segmentProvider = null;
        analyseService = null;
        loadable = true;
        return super.shutdown();
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
        SegmentContainer.NORMAL.remove(new SourceKey(sourceKey));
    }


    private RpcFuture queryRemoteNodeNode(String jsonString, SegmentDestination remoteURI) throws Exception {
        if (null == remoteURI) {
            QueryBean queryBean = queryBeanFactory.create(jsonString, false);
            remoteURI = queryBean.getQueryDestination();
        }
        Assert.notNull(remoteURI);
        String address = remoteURI.getAddress();
        String methodName = remoteURI.getMethodName();
        SwiftLoggers.getLogger().debug("Execute remote query from {} -> {}, segment is {}", remoteURI.getCurrentNode(), address, remoteURI.getSegmentId());
        Class clazz = remoteURI.getServiceClass();
        return ClusterCommonUtils.runAsyncRpc(address, clazz, ServiceMethodRegistry.INSTANCE.getMethodByName(methodName), jsonString);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.ANALYSE;
    }

    @Override
    public SwiftResultSet getQueryResult(QueryBean info) throws Exception {
        return analyseService.getQueryResult(info);
    }

    private void loadSelfSegmentDestination(SwiftClusterSegmentService clusterSegmentService) {
//        SwiftClusterSegmentService clusterSegmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);
//        clusterSegmentService.setClusterId(getID());
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
}