package com.fr.swift.cluster.service;

import com.fr.swift.annotation.RpcMethod;
import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.RpcServiceType;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.event.global.PushSegLocationRpcEvent;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.segment.impl.SegmentLocationInfoImpl;
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
import java.util.Arrays;
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
@RpcService(type = RpcServiceType.INTERNAL, value = RealtimeService.class)
public class ClusterRealTimeServiceImpl extends AbstractSwiftService implements ClusterRealTimeService, Serializable {
    private static final long serialVersionUID = 946204307880678794L;

    @Autowired
    private transient ServiceTaskExecutor taskExecutor;

    @Autowired(required = false)
    private RealtimeService realtimeService;

    private transient HashSet<SourceKey> existsTableKey;

    @Override
    public boolean start() throws SwiftServiceException {
        List<com.fr.swift.service.SwiftService> services = ServiceBeanFactory.getSwiftServiceByNames(Collections.singleton("realtime"));
        realtimeService = (RealtimeService) services.get(0);
        realtimeService.setId(getID());
        realtimeService.start();
        SegmentLocationInfo info = loadSelfSegmentDestination();
        if (null != info) {
            rpcSegmentLocation(new PushSegLocationRpcEvent(info));
        }
        existsTableKey = new HashSet<SourceKey>();
        return super.start();
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
    public String getID() {
        return ClusterSelector.getInstance().getFactory().getCurrentId();
    }


    @Override
    @RpcMethod(methodName = "realtimeDelete")
    public boolean delete(SourceKey sourceKey, Where where) throws Exception {
        return realtimeService.delete(sourceKey, where);
    }

    @Override
    @RpcMethod(methodName = "recover")
    public void recover(List<SegmentKey> tableKeys) throws Exception {
        realtimeService.recover(tableKeys);
    }

    @Override
    @RpcMethod(methodName = "realTimeQuery")
    public SwiftResultSet query(String queryInfo) throws Exception {
        return realtimeService.query(queryInfo);
    }

    private void rpcSegmentLocation(PushSegLocationRpcEvent event) {
        try {
            RpcFuture future = ClusterCommonUtils.asyncCallMaster(event);
            future.addCallback(new AsyncRpcCallback() {
                @Override
                public void success(Object result) {
                    logger.info("rpcTrigger success! ");
                }

                @Override
                public void fail(Exception e) {
                    logger.error("rpcTrigger error! ", e);
                }
            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    protected SegmentDestination createSegmentDestination(SegmentKey segmentKey) {
        String clusterId = ClusterSelector.getInstance().getFactory().getCurrentId();
        return new SegmentDestinationImpl(clusterId, segmentKey.toString(), segmentKey.getOrder(), ClusterRealTimeService.class, "realTimeQuery");
    }

    protected SegmentDestination createSegmentDestination(SourceKey segmentKey) {
        String clusterId = ClusterSelector.getInstance().getFactory().getCurrentId();
        return new SegmentDestinationImpl(clusterId, null, -1, ClusterRealTimeService.class, "realTimeQuery");
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.REAL_TIME;
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
                    if (segmentKey.getStoreType() != Types.StoreType.FINE_IO) {
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
        List<SegmentDestination> list = Arrays.asList(createSegmentDestination(sourceKey));
        map.put(sourceKey.getId(), list);
        return new SegmentLocationInfoImpl(ServiceType.REAL_TIME, map);
    }
}
