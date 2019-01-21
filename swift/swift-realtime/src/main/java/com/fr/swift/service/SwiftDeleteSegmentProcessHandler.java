package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.AbstractProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.DeleteSegmentProcessHandler;
import com.fr.swift.cluster.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.service.SwiftClusterSegmentService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * @author anchore
 * @date 2018/11/9
 */
public class SwiftDeleteSegmentProcessHandler extends AbstractProcessHandler<Map<URL, List<SegmentKey>>> implements DeleteSegmentProcessHandler {

    private SwiftClusterSegmentService segSvc = SwiftContext.get().getBean(SwiftClusterSegmentService.class);

    public SwiftDeleteSegmentProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    public Object processResult(Method method, Target target, Object... args) throws Throwable {
        Map<URL, List<SegmentKey>> urlToNeedUploadSegKeys = processUrl(target, args);
        Class<?> proxyClass = method.getDeclaringClass();
        Class<?>[] proxyMethodParamTypes = method.getParameterTypes();

        Map<URL, Future<?>> futures = new HashMap<URL, Future<?>>();
        for (Entry<URL, List<SegmentKey>> entry : urlToNeedUploadSegKeys.entrySet()) {
            URL url = entry.getKey();
            Invoker invoker = invokerCreator.createAsyncInvoker(proxyClass, url);
            args[args.length - 1] = entry.getValue();

            try {
                Future<?> future = (Future<?>) invoke(invoker, proxyClass, method, method.getName(), proxyMethodParamTypes, args);
                futures.put(url, future);
            } catch (Throwable throwable) {
                SwiftLoggers.getLogger().error(throwable);
            }
        }

        boolean totalResult = true;
        for (Entry<URL, Future<?>> entry : futures.entrySet()) {
            try {
                Boolean result = (Boolean) entry.getValue().get();
                SwiftLoggers.getLogger().info("delete segment on {} returned {}", entry.getKey(), result);
                totalResult &= result == null ? false : result;
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
                totalResult = false;
            }
        }

        return totalResult;
    }

    @Override
    public Map<URL, List<SegmentKey>> processUrl(Target target, Object... args) {
        if (!SwiftProperty.getProperty().isCluster()) {
            return Collections.singletonMap(null, Collections.<SegmentKey>emptyList());
        }

        SourceKey tableKey = (SourceKey) args[0];
        switch (target) {
            case HISTORY:
                return getNeedUploadSegKeysByService(tableKey, ServiceType.HISTORY);
            case REAL_TIME:
                return getNeedUploadSegKeysByService(tableKey, ServiceType.REAL_TIME);
            default:
                return Collections.emptyMap();
        }
    }

    private Map<URL, List<SegmentKey>> getNeedUploadSegKeysByService(SourceKey tableKey, ServiceType type) {
        HashMap<URL, List<SegmentKey>> urlToNeedUploadSegKeys = new HashMap<URL, List<SegmentKey>>();

        Set<SegmentKey> uploadedSegKeys = new HashSet<SegmentKey>();
        Map<String, ClusterEntity> services = ClusterSwiftServerService.getInstance().getClusterEntityByService(type);

        for (String clusterId : services.keySet()) {
            Map<SourceKey, List<SegmentKey>> segKeys = segSvc.getOwnSegments(clusterId);
            URL url = UrlSelector.getInstance().getFactory().getURL(clusterId);
            if (!segKeys.containsKey(tableKey)) {
                urlToNeedUploadSegKeys.put(url, Collections.<SegmentKey>emptyList());
                continue;
            }

            List<SegmentKey> ownSegKeys = segKeys.get(tableKey);

            // 过滤掉transient seg
            for (Iterator<SegmentKey> itr = ownSegKeys.iterator(); itr.hasNext(); ) {
                if (itr.next().getStoreType().isTransient()) {
                    itr.remove();
                }
            }

            // 去重
            ownSegKeys.removeAll(uploadedSegKeys);
            urlToNeedUploadSegKeys.put(url, ownSegKeys);

            uploadedSegKeys.addAll(ownSegKeys);
        }

        return urlToNeedUploadSegKeys;
    }
}