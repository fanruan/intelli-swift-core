package com.fr.swift.boot.synchronizer;

import com.fr.cluster.ClusterBridge;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.event.global.CheckMasterEvent;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rm.view.NodeJoinedView;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class created on 2018/11/9
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class MasterSynchronizer {

    private final static long SYNC_TIME = 10000L;

    private  ScheduledExecutorService service;

    public void start() {
        SwiftLoggers.getLogger().info(MasterSynchronizerRunnable.THREAD_NAME + " start!");
        service = SwiftExecutors.newScheduledThreadPool(1, new PoolThreadFactory(MasterSynchronizerRunnable.THREAD_NAME));
        service.scheduleAtFixedRate(new MasterSynchronizerRunnable(), SYNC_TIME, SYNC_TIME, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        SwiftLoggers.getLogger().info(MasterSynchronizerRunnable.THREAD_NAME + " end!");
        service.shutdown();

    }

    private class MasterSynchronizerRunnable implements Runnable {

        private static final String THREAD_NAME = "MasterSynchronizerRunnable";

        @Override
        public void run() {
            if (!NodeJoinedView.getInstance().isEmpty()) {
                ProxyFactory factory = ProxySelector.getInstance().getFactory();
                Set<String> syncedNodes = new HashSet<String>();
                for (String node : NodeJoinedView.getInstance().getNodes()) {
                    try {
                        if (ClusterBridge.getView().getNodeById(node) != null) {
                            SwiftServiceListenerHandler proxy = factory.getProxy(SwiftServiceListenerHandler.class);
                            proxy.trigger(new CheckMasterEvent());
                            syncedNodes.add(node);
                        }
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().warn("Node {} sync master failed!", node, e);
                    }
                }
                NodeJoinedView.getInstance().nodesRemove(syncedNodes);
            }
        }
    }
}
