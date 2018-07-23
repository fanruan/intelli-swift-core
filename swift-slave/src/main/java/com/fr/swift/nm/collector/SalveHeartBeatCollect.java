package com.fr.swift.nm.collector;

import com.fr.swift.Collect;
import com.fr.swift.cluster.service.SlaveService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.heart.HeartBeatInfo;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.nm.service.SwiftSlaveService;
import com.fr.swift.util.concurrent.SwiftExecutors;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SalveHeartBeatCollect implements Collect {

    private SlaveService slaveService;

    private Thread thread;

    public SalveHeartBeatCollect() {
        this.slaveService = SwiftContext.get().getBean("swiftSlaveService", SwiftSlaveService.class);
    }

    @Override
    public void startCollect() {
        SwiftLoggers.getLogger().debug(SlaveHeartBeatRunnable.THREAD_NAME + " start!");
        thread = SwiftExecutors.newThread(new SlaveHeartBeatRunnable(), SlaveHeartBeatRunnable.THREAD_NAME);
        thread.start();
    }

    @Override
    public void stopCollect() {
        SwiftLoggers.getLogger().debug(SlaveHeartBeatRunnable.THREAD_NAME + " interrupt!");
        thread.interrupt();
    }

    private class SlaveHeartBeatRunnable implements Runnable {

        private static final String THREAD_NAME = "SlaveHeartBeatRunnable";

        @Override
        public void run() {
            try {
                while (true) {
                    try {
                        HeartBeatInfo heartBeatInfo = new HeartBeatInfo();
                        slaveService.collectHeartBeat(heartBeatInfo);
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().error(e);
                    }
                    Thread.sleep(10000l);
                }
            } catch (InterruptedException ite) {
                SwiftLoggers.getLogger().error(ite);
            }
        }
    }
}
