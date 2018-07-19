package com.fr.swift.rm.collector;

import com.fr.swift.Collect;
import com.fr.swift.cluster.service.MasterService;
import com.fr.swift.container.NodeContainer;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.heart.HeartBeatInfo;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rm.service.SwiftMasterService;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.Date;
import java.util.List;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description 心跳超时检测
 * @since Advanced FineBI 5.0
 */
public class MasterHeatbeatCollect implements Collect {

//    private MasterService masterService = SwiftContext.getInstance().getBean("swiftSlaveService", SwiftMasterService.class);

    private Thread thread;

    public MasterHeatbeatCollect() {
    }

    @Override
    public void startCollect() {
        SwiftLoggers.getLogger().info(MasterHeartBeatRunnable.THREAD_NAME + " start!");
        thread = SwiftExecutors.newThread(new MasterHeartBeatRunnable(), MasterHeartBeatRunnable.THREAD_NAME);
        thread.start();
    }

    @Override
    public void stopCollect() {
        SwiftLoggers.getLogger().info(MasterHeartBeatRunnable.THREAD_NAME + " end!");
        thread.interrupt();
    }

    private class MasterHeartBeatRunnable implements Runnable {

        private static final String THREAD_NAME = "MasterHeartBeatRunnable";

        @Override
        public void run() {
            try {
                while (true) {
                    try {
                        List<HeartBeatInfo> allHeartBeatInfo = NodeContainer.getInstance().getAllHeartBeatInfos();
                        long nowTime = new Date().getTime();
                        for (HeartBeatInfo heartBeatInfo : allHeartBeatInfo) {
                            long diffTime = nowTime - heartBeatInfo.getHeartbeatTime().getTime();
                            if (diffTime > 30000l && diffTime <= 60000l) {
                                //todo 告警
                                SwiftLoggers.getLogger().error("warn!!!!!!!!!!!");
                            } else if (diffTime > 60000l) {
                                //todo 中断
                                SwiftLoggers.getLogger().error("error!!!!!!!!!!!");
                            }
                        }
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
