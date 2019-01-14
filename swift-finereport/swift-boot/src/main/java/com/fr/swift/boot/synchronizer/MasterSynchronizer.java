package com.fr.swift.boot.synchronizer;

import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.event.global.CheckMasterEvent;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rm.view.NodeJoinedView;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class created on 2018/11/9
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class MasterSynchronizer {

    private final static MasterSynchronizer INSTANCE = new MasterSynchronizer();

    public static MasterSynchronizer getInstance() {
        return INSTANCE;
    }

    private MasterSynchronizer() {
    }

    private final static long SYNC_TIME = 10000L;

    private ScheduledExecutorService service;

    private volatile boolean running = false;

    private Lock lock = new ReentrantLock();

    public void start() {
        lock.lock();
        try {
            if (!running) {
                SwiftLoggers.getLogger().info(MasterSynchronizerRunnable.THREAD_NAME + " start!");
                service = SwiftExecutors.newScheduledThreadPool(1, new PoolThreadFactory(MasterSynchronizerRunnable.THREAD_NAME));
                service.scheduleAtFixedRate(new MasterSynchronizerRunnable(), SYNC_TIME, SYNC_TIME, TimeUnit.MILLISECONDS);
                running = true;
            } else {
                SwiftLoggers.getLogger().warn(MasterSynchronizerRunnable.THREAD_NAME + " is running ! Can't be started again!");
            }
        } finally {
            lock.unlock();
        }
    }

    public void stop() {
        lock.lock();
        try {
            if (running) {
                SwiftLoggers.getLogger().info(MasterSynchronizerRunnable.THREAD_NAME + " end!");
                service.shutdown();
                running = false;
            } else {
                SwiftLoggers.getLogger().warn(MasterSynchronizerRunnable.THREAD_NAME + " is not running ! Can't be stopped!");
            }
        } finally {
            lock.unlock();
        }
    }

    private class MasterSynchronizerRunnable implements Runnable {

        private static final String THREAD_NAME = "MasterSynchronizerRunnable";

        @Override
        public void run() {
            if (!NodeJoinedView.getInstance().isEmpty()) {
                try {
                    ProxyFactory factory = ProxySelector.getInstance().getFactory();
                    Set<String> syncedNodes = new HashSet<String>();
                    RemoteSender sender = factory.getProxy(RemoteSender.class);
                    syncedNodes.addAll((List<String>) sender.appointTrigger(NodeJoinedView.getInstance().getNodes(), new CheckMasterEvent()));
                    NodeJoinedView.getInstance().nodesRemove(syncedNodes);
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        }
    }
}
