package com.fr.swift.process.handler;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.AbstractProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.AliveNodesProcessHandler;
import com.fr.swift.cluster.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.service.ServiceType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/12/28
 *
 * @author Lucifer
 * @description
 */
public class SwiftAliveNodesProcessHandler extends AbstractProcessHandler implements AliveNodesProcessHandler {

    public SwiftAliveNodesProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    protected List<URL> processUrl(Target target, Object... args) {
        Map<String, ClusterEntity> analyseServices = checkAnalyseServiceEmpty();
        List<URL> urlList = new ArrayList<URL>();
        for (Map.Entry<String, ClusterEntity> clusterEntityEntry : analyseServices.entrySet()) {
            urlList.add(UrlSelector.getInstance().getFactory().getURL(clusterEntityEntry.getKey()));
        }
        return urlList;
    }

    @Override
    public Object processResult(Method method, Target target, Object... args) throws Throwable {
        final Class proxyClass = method.getDeclaringClass();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final String methodName = method.getName();
        List<URL> urlList = processUrl(target, args);
        List<RpcFuture> rpcFutures = new ArrayList<RpcFuture>();
        for (URL url : urlList) {
            final Invoker invoker = invokerCreator.createAsyncInvoker(proxyClass, url);
            RpcFuture rpcFuture = (RpcFuture) invoke(invoker, proxyClass,
                    method, methodName, parameterTypes, args);
            rpcFutures.add(rpcFuture);
        }
        for (RpcFuture rpcFuture : rpcFutures) {
            rpcFuture.get();
        }
        return null;
    }

    private Map<String, ClusterEntity> checkAnalyseServiceEmpty() {
        Map<String, ClusterEntity> analyseServices = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE);
        if (null == analyseServices || analyseServices.isEmpty()) {
            throw new RuntimeException("Cannot find analyse service");
        }
        return analyseServices;
    }
}
