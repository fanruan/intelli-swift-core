package com.fr.swift.basics.base.handler;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.basics.handler.AppointProcessHandler;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.log.SwiftLoggers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * This class created on 2019/1/14
 *
 * @author Lucifer
 * @description
 */
@SwiftBean
@SwiftScope("prototype")
@RegisteredHandler(AppointProcessHandler.class)
// TODO: 2019/1/22 等an老师改完后待弃用
public class SwiftAppointProcessHandler extends AbstractProcessHandler implements AppointProcessHandler {
    public SwiftAppointProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    /**
     * @param method
     * @param targets
     * @param args   args[0]固定参数Collection<String> urls
     *               其他是具体参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
        Class proxyClass = method.getDeclaringClass();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String methodName = method.getName();
        Collection<URL> urls = processUrl(targets, args[0]);
        final List<Object> resultList = new ArrayList<Object>();
        final CountDownLatch latch = new CountDownLatch(urls.size());
        for (final URL url : urls) {
            Invoker invoker = invokerCreator.createAsyncInvoker(proxyClass, url);
            RpcFuture rpcFuture = (RpcFuture) invoke(invoker, proxyClass, method, methodName, parameterTypes, args);
            rpcFuture.addCallback(new AsyncRpcCallback() {
                @Override
                public void success(final Object result) {
                    try {
                        resultList.add(result);
                    } finally {
                        latch.countDown();
                    }
                }

                @Override
                public void fail(Exception e) {
                    latch.countDown();
                    SwiftLoggers.getLogger().error(e);
                }
            });
        }
        latch.await();
        return resultList;
    }

    @Override
    public Collection<URL> processUrl(Target[] targets, Object... args) {
        Set<URL> urls = new HashSet<URL>();
        Collection<String> urlStrs = (Collection<String>) args[0];
        for (String urlStr : urlStrs) {
            URL url = UrlSelector.getInstance().getFactory().getURL(urlStr);
            urls.add(url);
        }
        return urls;
    }
}
