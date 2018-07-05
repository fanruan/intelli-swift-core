package com.fr.log.impl;

import com.fr.cluster.entry.ClusterTicketKey;
import com.fr.general.LogOperatorFactory;
import com.fr.module.Activator;
import com.fr.module.extension.Prepare;
import com.fr.swift.adaptor.log.LogOperatorProxy;
import com.fr.swift.frrpc.SwiftClusterTicket;
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
        LogOperatorFactory.registerLogOperatorProvider(LogOperatorProxy.getInstance());
        previousLoggerFactory = SwiftLoggers.getLoggerFactory();
        SwiftLoggers.setLoggerFactory(new SwiftFrLoggers());
    }

    @Override
    public void stop() {
        LogOperatorFactory.registerLogOperatorProvider(null);
        SwiftLoggers.setLoggerFactory(previousLoggerFactory);
    }

    @Override
    public void prepare() {
        addMutable(ClusterTicketKey.KEY, SwiftClusterTicket.getInstance());
    }
}