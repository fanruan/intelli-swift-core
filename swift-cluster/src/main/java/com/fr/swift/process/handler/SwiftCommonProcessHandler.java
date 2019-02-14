package com.fr.swift.process.handler;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.InvokerType;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.BaseProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.CommonProcessHandler;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.util.MonitorUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author yee
 * @date 2018/10/29
 */
@SwiftBean
@SwiftScope("prototype")
@RegisteredHandler(CommonProcessHandler.class)
public class SwiftCommonProcessHandler extends BaseProcessHandler<List<URL>> implements CommonProcessHandler {

    public SwiftCommonProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
        MonitorUtil.start();
        Class proxyClass = method.getDeclaringClass();
        Class<?>[] parameterTypes = method.getParameterTypes();
        List<URL> urls = processUrl(targets);
        String methodName = method.getName();
        if (urls.isEmpty() && invokerCreator.getType().equals(InvokerType.LOCAL)) {
            Invoker invoker = invokerCreator.createSyncInvoker(proxyClass, null);
            return invoke(invoker, proxyClass, method, methodName, parameterTypes, args);
        }
        for (URL url : urls) {
            Invoker invoker = invokerCreator.createAsyncInvoker(proxyClass, url);
            invoke(invoker, proxyClass, method, methodName, parameterTypes, args);
        }
        MonitorUtil.finish(methodName);
        return null;
    }

    @Override
    protected Object mergeResult(List resultList, Object... args) {
        return null;
    }

    @Override
    public List<URL> processUrl(Target[] targets, Object... args) {
        Set<String> clusterIds = new HashSet<String>();
        for (Target target : targets) {
            switch (target) {
                case ANALYSE:
                    clusterIds.addAll(ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE).keySet());
                    break;
                case HISTORY:
                    clusterIds.addAll(ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.HISTORY).keySet());
                    break;
                case REAL_TIME:
                    clusterIds.addAll(ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.REAL_TIME).keySet());
                    break;
                case INDEXING:
                    clusterIds.addAll(ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.INDEXING).keySet());
                    break;
                case ALL:
                    clusterIds.addAll(ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE).keySet());
                    clusterIds.addAll(ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.HISTORY).keySet());
                    clusterIds.addAll(ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.REAL_TIME).keySet());
                    clusterIds.addAll(ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.INDEXING).keySet());
                    break;
                default:
                    return Collections.emptyList();
            }
        }
        if (!clusterIds.isEmpty()) {
            List<URL> urls = new ArrayList<URL>();
            for (String clusterId : clusterIds) {
                urls.add(UrlSelector.getInstance().getFactory().getURL(clusterId));
            }
            return urls;
        }
        return Collections.emptyList();
    }
}
