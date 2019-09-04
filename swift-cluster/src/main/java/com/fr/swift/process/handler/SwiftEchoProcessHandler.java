package com.fr.swift.process.handler;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.AbstractProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.EchoProcessHandler;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.cluster.ClusterEntity;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.SwiftService;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 8/30/2019
 */
@SwiftBean
@SwiftScope("prototype")
@RegisteredHandler(EchoProcessHandler.class)
public class SwiftEchoProcessHandler extends AbstractProcessHandler<Set> implements EchoProcessHandler {

    public SwiftEchoProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    protected Set<URL> processUrl(Target[] targets, Object... args) {
        Set<URL> urlSets = new HashSet<>();
        for (ClusterEntity entity : ClusterSwiftServerService.getInstance().getClusterEntityByService(((SwiftService) args[0]).getServiceType()).values()) {
            urlSets.add(entity.getUrl());
        }
        urlSets.remove(UrlSelector.getInstance().getFactory().getURL(ClusterSelector.getInstance().getFactory().getMasterId()));
        return urlSets;
    }

    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
        Set<String> healthyUrls = new HashSet<>();
        try {
            for (URL url : processUrl(targets, args)) {
                Class proxy = method.getDeclaringClass();
                Class<?>[] parameterTypes = method.getParameterTypes();
                String methodName = method.getName();
                Invoker invoker = invokerCreator.createSyncInvoker(proxy, url);
                try {
                    healthyUrls.addAll((Set<String>) invoke(invoker, proxy, method, methodName, parameterTypes, args));
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error("RPCService inspect failed:", e);
                    break;
                }
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return healthyUrls;
    }
}
