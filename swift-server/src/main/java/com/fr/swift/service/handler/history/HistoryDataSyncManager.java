package com.fr.swift.service.handler.history;

import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.Result;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.event.history.HistoryLoadRpcEvent;
import com.fr.swift.invocation.SwiftInvocation;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.client.AsyncRpcCallback;
import com.fr.swift.rpc.client.async.RpcFuture;
import com.fr.swift.rpc.url.RPCDestination;
import com.fr.swift.rpc.url.RPCUrl;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.impl.SegmentLocationInfoImpl;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.service.ClusterSwiftServerService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.entity.ClusterEntity;
import com.fr.swift.service.handler.base.Handler;
import com.fr.swift.service.handler.history.rule.DataSyncRule;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class HistoryDataSyncManager implements Handler<HistoryLoadRpcEvent> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(HistoryDataSyncManager.class);
    @Autowired(required = false)
    private SwiftClusterSegmentService clusterSegmentService;
    private DataSyncRule rule = DataSyncRule.DEFAULT;

    public void setRule(DataSyncRule rule) {
        this.rule = rule;
    }

    public <S extends Serializable> S handle(HistoryLoadRpcEvent event) {
        Map<String, ClusterEntity> services = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.HISTORY);
        ProxyFactory factory = ProxySelector.getInstance().getFactory();
        Map<String, List<SegmentKey>> needLoadSegment = event.getContent();
        Map<String, List<SegmentKey>> keys = clusterSegmentService.getClusterSegments();
        Iterator<String> keyIterator = services.keySet().iterator();
        Map<String, List<SegmentKey>> exists = new HashMap<String, List<SegmentKey>>();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            exists.put(key, keys.get(key));
        }
        Map<String, List<SegmentDestination>> destinations = new HashMap<String, List<SegmentDestination>>();
        Map<String, Set<URI>> result = rule.calculate(exists, needLoadSegment, destinations);
        keyIterator = result.keySet().iterator();
        try {
            final CountDownLatch latch = new CountDownLatch(result.size());
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();

                ClusterEntity entity = services.get(key);
                Invoker invoker = factory.getInvoker(null, entity.getServiceClass(), new RPCUrl(new RPCDestination(key)), false);
                Method method = entity.getServiceClass().getMethod("load", Set.class);
                Result invokeResult = invoker.invoke(new SwiftInvocation(method, new Object[]{result.get(key)}));
                RpcFuture future = (RpcFuture) invokeResult.getValue();
                future.addCallback(new AsyncRpcCallback() {
                    @Override
                    public void success(Object result) {
                        latch.countDown();
                    }

                    @Override
                    public void fail(Exception e) {
                        LOGGER.error("Load failed! ", e);
                        latch.countDown();
                    }
                });

            }
            latch.await();

            Map<String, ClusterEntity> analyseServices = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE);
            Iterator<Map.Entry<String, ClusterEntity>> iterator = analyseServices.entrySet().iterator();
            while (iterator.hasNext()) {
                final long start = System.currentTimeMillis();
                Map.Entry<String, ClusterEntity> entity = iterator.next();
                String address = entity.getKey();
                Class clazz = entity.getValue().getServiceClass();
                Invoker invoker = factory.getInvoker(null, clazz, new RPCUrl(new RPCDestination(address)), false);
                Method method = clazz.getMethod("updateSegmentInfo", SegmentLocationInfo.class, SegmentLocationInfo.UpdateType.class);
                Result invokeResult = invoker.invoke(new SwiftInvocation(method, new Object[]{new SegmentLocationInfoImpl(ServiceType.HISTORY, destinations), SegmentLocationInfo.UpdateType.ALL}));
                RpcFuture future = (RpcFuture) invokeResult.getValue();
                future.addCallback(new AsyncRpcCallback() {
                    @Override
                    public void success(Object result) {
                        LOGGER.info(String.format("Update segmentInfo cost: %d ms", System.currentTimeMillis() - start));
                    }

                    @Override
                    public void fail(Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

}
