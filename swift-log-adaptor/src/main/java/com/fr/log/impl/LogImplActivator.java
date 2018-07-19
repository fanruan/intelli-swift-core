package com.fr.log.impl;

import com.fr.cluster.entry.ClusterTicketKey;
import com.fr.intelli.record.MetricRegistry;
import com.fr.module.Activator;
import com.fr.module.extension.Prepare;
import com.fr.swift.adaptor.log.MetricProxy;
import com.fr.swift.core.cluster.SwiftClusterTicket;

/**
 * @author anchore
 * @date 2018/4/26
 */
public class LogImplActivator extends Activator implements Prepare {

    @Override
    public void start() {
        MetricRegistry.register(MetricProxy.getInstance());
    }

    @Override
    public void stop() {
        MetricRegistry.reset();
    }

    @Override
    public void prepare() {
        addMutable(ClusterTicketKey.KEY, SwiftClusterTicket.getInstance());
    }
}