package com.fr.swift.service;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreater;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.AbstractProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.DeleteSegmentProcessHandler;
import com.fr.swift.cluster.entity.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.log.SwiftLoggers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/11/9
 */
public class SwiftDeleteSegmentProcessHandler extends AbstractProcessHandler<List<URL>> implements DeleteSegmentProcessHandler {

    public SwiftDeleteSegmentProcessHandler(InvokerCreater invokerCreater) {
        super(invokerCreater);
    }

    @Override
    public Object processResult(Method method, Target target, Object... args) throws Throwable {
        Class<?> proxyClass = method.getDeclaringClass();
        Class<?>[] proxyMethodParamTypes = method.getParameterTypes();

        boolean totalResult = true;
        for (URL url : processUrl(target, args)) {
            Invoker invoker = invokerCreater.createSyncInvoker(proxyClass, url);
            Object result = invoke(invoker, proxyClass, method, method.getName(), proxyMethodParamTypes, args);
            SwiftLoggers.getLogger().debug("{} returned {}", url, result);
            totalResult &= ((Boolean) result);
        }
        return totalResult;
    }

    @Override
    public List<URL> processUrl(Target target, Object... args) {
        switch (target) {
            case HISTORY:
                return getUrls(ServiceType.HISTORY);
            case REAL_TIME:
                return getUrls(ServiceType.REAL_TIME);
            default:
                return Collections.emptyList();
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