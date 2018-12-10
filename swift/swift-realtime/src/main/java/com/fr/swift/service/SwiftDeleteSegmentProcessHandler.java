package com.fr.swift.service;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.AbstractProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.DeleteSegmentProcessHandler;
import com.fr.swift.cluster.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.util.Optional;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author anchore
 * @date 2018/11/9
 */
public class SwiftDeleteSegmentProcessHandler extends AbstractProcessHandler<Optional<List<URL>>> implements DeleteSegmentProcessHandler {

    public SwiftDeleteSegmentProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    public Object processResult(Method method, Target target, Object... args) throws Throwable {
        Optional<List<URL>> optionalUrls = processUrl(target, args);
        Class<?> proxyClass = method.getDeclaringClass();
        Class<?>[] proxyMethodParamTypes = method.getParameterTypes();

        if (!optionalUrls.isPresent()) {
            // local invoke
            Invoker invoker = invokerCreator.createSyncInvoker(proxyClass, null);
            return invoke(invoker, proxyClass, method, method.getName(), proxyMethodParamTypes, args);
        }

        List<URL> urls = optionalUrls.get();
        if (urls.isEmpty()) {
            return true;
        }

        List<Future<?>> futures = new ArrayList<Future<?>>();
        for (URL url : urls) {
            // remote invoke
            Invoker invoker = invokerCreator.createAsyncInvoker(proxyClass, url);
            Future<?> future = (Future<?>) invoke(invoker, proxyClass, method, method.getName(), proxyMethodParamTypes, args);
            futures.add(future);
        }

        boolean totalResult = true;
        for (int i = 0; i < futures.size(); i++) {
            try {
                Boolean result = (Boolean) futures.get(i).get();
                SwiftLoggers.getLogger().debug("delete segment on {} returned {}", urls.get(i), result);
                totalResult &= result == null ? false : result;
            } catch (Exception e) {
                SwiftLoggers.getLogger().debug(e);
                totalResult = false;
            }
        }
        return totalResult;
    }

    @Override
    public Optional<List<URL>> processUrl(Target target, Object... args) {
        if (!ClusterSelector.getInstance().getFactory().isCluster()) {
            return Optional.empty();
        }
        switch (target) {
            case HISTORY:
                return Optional.of(getUrls(ServiceType.HISTORY));
            case REAL_TIME:
                return Optional.of(getUrls(ServiceType.REAL_TIME));
            default:
                return Optional.of(Collections.<URL>emptyList());
        }
    }

    private List<URL> getUrls(ServiceType type) {
        Map<String, ClusterEntity> services = ClusterSwiftServerService.getInstance().getClusterEntityByService(type);
        List<String> clusterIds = new ArrayList<String>(services.keySet());
        List<URL> urls = new ArrayList<URL>(clusterIds.size());
        for (String clusterId : clusterIds) {
            urls.add(UrlSelector.getInstance().getFactory().getURL(clusterId));
        }
        return urls;
    }
}