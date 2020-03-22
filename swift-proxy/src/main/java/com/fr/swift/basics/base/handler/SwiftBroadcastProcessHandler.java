package com.fr.swift.basics.base.handler;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.handler.BroadcastProcessHandler;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.db.Where;
import com.fr.swift.local.LocalUrl;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author lucifer
 * @date 2020/3/18
 * @description
 * @since swift 1.1
 */
@SwiftBean
@SwiftScope("prototype")
@RegisteredHandler(BroadcastProcessHandler.class)
public class SwiftBroadcastProcessHandler extends BaseProcessHandler implements BroadcastProcessHandler {

    public SwiftBroadcastProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
        SourceKey tableKey = (SourceKey) args[0];
        Where where = (Where) args[1];
        final Class<?> proxyClass = method.getDeclaringClass();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final String methodName = method.getName();
        List<URL> urls = processUrl(targets);
        List<Future> futures = new ArrayList<>();
        for (URL url : urls) {
            final Invoker<?> invoker = invokerCreator.createAsyncInvoker(proxyClass, url);
            RpcFuture<?> rpcFuture = (RpcFuture<?>) invoke(invoker, proxyClass, method, methodName, parameterTypes, args);
            futures.add(rpcFuture);
        }
        for (Future future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
        return true;
    }

    @Override
    protected Object mergeResult(List resultList, Object... args) throws Throwable {
        return null;
    }

    @Override
    protected List<URL> processUrl(Target[] targets, Object... args) {
        return Collections.singletonList(new LocalUrl());
    }
}
