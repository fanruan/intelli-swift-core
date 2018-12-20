package com.fr.log.impl;

import com.fr.intelli.record.MetricRegistry;
import com.fr.intelli.record.scene.Metric;
import com.fr.module.Activator;
import com.fr.module.extension.Prepare;

import java.lang.reflect.Proxy;

/**
 * @author anchore
 * @date 2018/4/26
 */
public class LogImplActivator extends Activator implements Prepare {

    @Override
    public void start() {
        Metric metric = (Metric) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{Metric.class},
                MetricInvocationHandler.getInstance());
        MetricRegistry.register(metric);
    }

    @Override
    public void stop() {
        MetricRegistry.reset();
    }

    @Override
    public void prepare() {
    }
}