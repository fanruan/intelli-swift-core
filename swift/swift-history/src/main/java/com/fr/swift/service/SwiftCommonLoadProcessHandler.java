package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.AbstractProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.CommonLoadProcessHandler;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.cluster.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.event.base.EventResult;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.MonitorUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * This class created on 2018/11/6
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean
@SwiftScope("prototype")
@RegisteredHandler(CommonLoadProcessHandler.class)
public class SwiftCommonLoadProcessHandler extends AbstractProcessHandler<Map<URL, Map<SourceKey, List<String>>>> implements CommonLoadProcessHandler {

    public SwiftCommonLoadProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    /**
     * @param method
     * @param targets
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
        Class proxyClass = method.getDeclaringClass();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String methodName = method.getName();
        try {
            MonitorUtil.start();
            SourceKey sourceKey = (SourceKey) args[0];
            Map<URL, Map<SourceKey, List<String>>> urlMap = processUrl(targets, args);

            final List<EventResult> resultList = new ArrayList<EventResult>();
            final CountDownLatch latch = new CountDownLatch(urlMap.size());
            for (final Map.Entry<URL, Map<SourceKey, List<String>>> urlMapEntry : urlMap.entrySet()) {

                Invoker invoker = invokerCreator.createAsyncInvoker(proxyClass, urlMapEntry.getKey());
                RpcFuture rpcFuture = (RpcFuture) invoke(invoker, proxyClass, method, methodName, parameterTypes, sourceKey, urlMapEntry.getValue());
                rpcFuture.addCallback(new AsyncRpcCallback() {
                    @Override
                    public void success(Object result) {
                        EventResult eventResult = new EventResult(urlMapEntry.getKey().getDestination().getId(), true);
                        resultList.add(eventResult);
                        latch.countDown();
                    }

                    @Override
                    public void fail(Exception e) {
                        EventResult eventResult = new EventResult(urlMapEntry.getKey().getDestination().getId(), false);
                        eventResult.setError(e.getMessage());
                        resultList.add(eventResult);
                        latch.countDown();
                    }
                });
            }
            latch.await();
            return resultList;
        } finally {
            MonitorUtil.finish(methodName);
        }
    }

    /**
     * args：需要load的seg信息
     * args[0]:sourcekey
     * args[1]:<segkey,uri string>
     * 根据传入的seg信息，遍历所有history节点，找到每个history节点的seg
     * 检验该seg是否在需要args中，是则needload，否则不需要。
     *
     * @param targets
     * @param args
     * @return 远程url
     */
    @Override
    public Map<URL, Map<SourceKey, List<String>>> processUrl(Target[] targets, Object... args) {
        SwiftClusterSegmentService clusterSegmentService = SwiftContext.get().getBean(SwiftClusterSegmentService.class);
        Map<String, ClusterEntity> services = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.HISTORY);

        SourceKey sourceKey = (SourceKey) args[0];
        Map<SegmentKey, List<String>> uris = (Map<SegmentKey, List<String>>) args[1];

        if (null == services || services.isEmpty()) {
            throw new RuntimeException("Cannot find history service");
        }
        Map<URL, Map<SourceKey, List<String>>> resultMap = new HashMap<URL, Map<SourceKey, List<String>>>();
        for (Map.Entry<String, ClusterEntity> servicesEntry : services.entrySet()) {
            String clusterId = servicesEntry.getKey();
            Map<SourceKey, List<SegmentKey>> map = clusterSegmentService.getOwnSegments(clusterId);
            List<SegmentKey> list = map.get(sourceKey);
            Set<String> needLoad = new HashSet<String>();
            if (!list.isEmpty()) {
                for (SegmentKey segmentKey : list) {
                    if (uris.containsKey(segmentKey)) {
                        needLoad.addAll(uris.get(segmentKey));
                    }
                }
            }
            if (!needLoad.isEmpty()) {
                Map<SourceKey, List<String>> loadMap = new HashMap<SourceKey, List<String>>();
                loadMap.put(sourceKey, new ArrayList<String>(needLoad));
                resultMap.put(UrlSelector.getInstance().getFactory().getURL(clusterId), loadMap);
            }
        }
        return resultMap;
    }
}
