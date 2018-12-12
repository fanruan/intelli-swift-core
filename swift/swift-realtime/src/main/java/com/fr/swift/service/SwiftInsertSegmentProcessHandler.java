package com.fr.swift.service;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.AbstractProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.InsertSegmentProcessHandler;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.util.Optional;

import java.lang.reflect.Method;

/**
 * @author anchore
 * @date 2018/11/13
 */
public class SwiftInsertSegmentProcessHandler extends AbstractProcessHandler<Optional<URL>> implements InsertSegmentProcessHandler {

    public SwiftInsertSegmentProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    protected Optional<URL> processUrl(Target target, Object... args) {
        if (ClusterSelector.getInstance().getFactory().isCluster()) {
            String clusterId = ClusterSelector.getInstance().getFactory().getCurrentId();
            URL url = UrlSelector.getInstance().getFactory().getURL(clusterId);
            return Optional.of(url);
        }

        return Optional.empty();
    }

    @Override
    public Object processResult(Method method, Target target, Object... args) throws Throwable {
        if (target != Target.REAL_TIME) {
            return null;
        }

        Optional<URL> url = processUrl(target, args);
        Class<?> proxyClass = method.getDeclaringClass();
        Class<?>[] proxyMethodParamTypes = method.getParameterTypes();

        Invoker invoker = invokerCreator.createSyncInvoker(proxyClass, url.isPresent() ? url.get() : null);
        return invoke(invoker, proxyClass, method, method.getName(), proxyMethodParamTypes, args);
    }
}