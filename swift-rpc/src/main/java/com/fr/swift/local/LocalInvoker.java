package com.fr.swift.local;

import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invocation;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.Result;
import com.fr.swift.basics.base.SwiftResult;
import com.fr.swift.util.Assert;

import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * This class created on 2018/5/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class LocalInvoker<T> implements Invoker<T> {

    private final T proxy;

    private final Class<T> type;

    private final URL url;

    private boolean sync = true;

    public LocalInvoker(T proxy, Class<T> type, URL url, boolean sync) {
        this(proxy, type, url);
        this.sync = sync;
    }

    public LocalInvoker(T proxy, Class<T> type, URL url) {
        Assert.notNull(proxy, "proxy == null");
        Assert.notNull(type, "interface == null");
        Assert.isInstanceOf(type, proxy, MessageFormat.format("{0} not implement interface {1}", proxy.getClass().getName(), type));

        this.proxy = proxy;
        this.type = type;
        this.url = url;
    }

    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public Result invoke(Invocation invocation) {
        try {
            return new SwiftResult(doInvoke(proxy, invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments()));
        } catch (Throwable e) {
            return new SwiftResult(e);
        }
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void destroy() {
    }

    protected Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Throwable {
        if (sync) {
            Method method = proxy.getClass().getMethod(methodName, parameterTypes);
            return method.invoke(proxy, arguments);
        } else {
            LocalFuture localFuture = new LocalFuture();
            LocalResult localResult = new LocalResult();
            try {
                Method method = proxy.getClass().getMethod(methodName, parameterTypes);
                Object result = method.invoke(proxy, arguments);
                localResult.setResult(result);
            } catch (Throwable e) {
                localResult.setException(e);
            }
            localFuture.done(localResult);
            return localFuture;
        }
    }
}
