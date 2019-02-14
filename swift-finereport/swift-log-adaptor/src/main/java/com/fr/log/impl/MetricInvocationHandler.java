package com.fr.log.impl;

import com.fr.intelli.record.MetricRegistry;
import com.fr.swift.adaptor.log.MetricProxy;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.structure.Pair;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 12/4/2018
 */
public class MetricInvocationHandler implements InvocationHandler {

    private final List<Pair<Method, Object[]>> invocations = new ArrayList<Pair<Method, Object[]>>();

    private volatile boolean allOver = false;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        synchronized (invocations) {
            if (allOver) {
                return method.invoke(MetricProxy.getInstance(), args);
            }
            invocations.add(Pair.of(method, args));
            return method.invoke(MetricRegistry.ZERO, args);
        }
    }

    public void doAfterSwiftContextInit() {
        synchronized (invocations) {
            invokeAll();
            MetricRegistry.register(MetricProxy.getInstance());
            allOver = true;
        }
    }

    private void invokeAll() {
        for (Pair<Method, Object[]> invocation : invocations) {
            try {
                invocation.getKey().invoke(MetricProxy.getInstance(), invocation.getValue());
                SwiftLoggers.getLogger().info("invoked {}", invocation);
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
        invocations.clear();
    }

    private static final MetricInvocationHandler INSTANCE = new MetricInvocationHandler();

    private MetricInvocationHandler() {
    }

    public static MetricInvocationHandler getInstance() {
        return INSTANCE;
    }
}