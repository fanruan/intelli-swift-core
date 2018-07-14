package com.fr.log.impl;

import com.fr.cluster.entry.ClusterTicketKey;
import com.fr.intelli.record.AccumulatorFactory;
import com.fr.module.Activator;
import com.fr.module.extension.Prepare;
import com.fr.swift.adaptor.log.AccumulatorProxy;
import com.fr.swift.core.rpc.SwiftClusterTicket;
import com.fr.swift.log.SwiftFrLoggers;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.function.Function;

/**
 * @author anchore
 * @date 2018/4/26
 */
public class LogImplActivator extends Activator implements Prepare {
    private Function<?, SwiftLogger> previousLoggerFactory;

    @Override
    public void start() {
        AccumulatorFactory.register(AccumulatorProxy.getInstance());
        previousLoggerFactory = SwiftLoggers.getLoggerFactory();
        SwiftLoggers.setLoggerFactory(new SwiftFrLoggers());
    }

    @Override
    public void stop() {
        AccumulatorFactory.reset();
        SwiftLoggers.setLoggerFactory(previousLoggerFactory);
    }

    @Override
    public void prepare() {
        addMutable(ClusterTicketKey.KEY, SwiftClusterTicket.getInstance());
    }
}