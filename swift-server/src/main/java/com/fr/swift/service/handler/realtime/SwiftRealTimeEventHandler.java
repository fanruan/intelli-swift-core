package com.fr.swift.service.handler.realtime;

import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.Result;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.event.base.AbstractRealTimeRpcEvent;
import com.fr.swift.invocation.SwiftInvocation;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.client.AsyncRpcCallback;
import com.fr.swift.rpc.client.async.RpcFuture;
import com.fr.swift.rpc.url.RPCDestination;
import com.fr.swift.rpc.url.RPCUrl;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.service.ClusterSwiftServerService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.entity.ClusterEntity;
import com.fr.swift.service.handler.base.Handler;
import com.fr.swift.service.handler.history.SwiftHistoryEventHandler;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class SwiftRealTimeEventHandler implements Handler<AbstractRealTimeRpcEvent> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftHistoryEventHandler.class);
    private ExecutorService realTimeService = Executors.newCachedThreadPool(new PoolThreadFactory(getClass()));

    @Autowired(required = false)
    private SwiftClusterSegmentService clusterSegmentService;

    @Override
    public <S extends Serializable> S handle(AbstractRealTimeRpcEvent event) {
        Map<String, ClusterEntity> realTimeServices = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.REAL_TIME);
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        try {
            switch (event.subEvent()) {
                case RECOVER:
                    final Map<String, List<SegmentKey>> map = clusterSegmentService.getClusterSegments();
                    Iterator<Map.Entry<String, ClusterEntity>> iterator = realTimeServices.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, ClusterEntity> entityEntry = iterator.next();
                        final String key = entityEntry.getKey();
                        ClusterEntity entity = entityEntry.getValue();
                        final List<SegmentKey> list = map.get(key);
                        if (null != list && !list.isEmpty()) {
                            final long start = System.currentTimeMillis();
                            Invoker invoker = factory.getInvoker(null, entity.getServiceClass(), new RPCUrl(new RPCDestination(key)), false);
                            Method method = entity.getServiceClass().getMethod("recover", List.class);
                            Result result = invoker.invoke(new SwiftInvocation(method, new Object[]{list}));
                            RpcFuture future = (RpcFuture) result.getValue();
                            future.addCallback(new AsyncRpcCallback() {
                                @Override
                                public void success(Object result) {
                                    LOGGER.info(String.format("clusterId: %s, recover cost: %d ms", key, (System.currentTimeMillis() - start)));
                                }

                                @Override
                                public void fail(Exception e) {
                                    LOGGER.error(String.format("clusterId: %s, recover error!", key), e);
                                }
                            });
                        }
                    }
                    return null;
                case MERGE:
                    final Map<String, List<SegmentKey>> mergeMap = clusterSegmentService.getClusterSegments();
                    Iterator<Map.Entry<String, ClusterEntity>> mergeIterator = realTimeServices.entrySet().iterator();
                    while (mergeIterator.hasNext()) {
                        Map.Entry<String, ClusterEntity> entityEntry = mergeIterator.next();
                        final String key = entityEntry.getKey();
                        ClusterEntity entity = entityEntry.getValue();
                        final List<SegmentKey> list = mergeMap.get(key);
                        if (null != list && !list.isEmpty()) {
                            final long start = System.currentTimeMillis();
                            Invoker invoker = factory.getInvoker(null, entity.getServiceClass(), new RPCUrl(new RPCDestination(key)), false);
                            Method method = entity.getServiceClass().getMethod("merge", List.class);
                            Result result = invoker.invoke(new SwiftInvocation(method, new Object[]{list}));
                            RpcFuture future = (RpcFuture) result.getValue();
                            future.addCallback(new AsyncRpcCallback() {
                                @Override
                                public void success(Object result) {
                                    LOGGER.info(String.format("clusterId: %s, merge cost: %d ms", key, (System.currentTimeMillis() - start)));
                                }

                                @Override
                                public void fail(Exception e) {
                                    LOGGER.error(String.format("clusterId: %s, merge error!", key), e);
                                }
                            });
                        }
                    }
                    return null;
            }
        } catch (Exception e) {
            LOGGER.error("handle error! ", e);
        }
        return null;
    }
}
