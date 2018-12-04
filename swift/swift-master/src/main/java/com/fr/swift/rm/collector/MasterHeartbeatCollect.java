package com.fr.swift.rm.collector;

import com.fr.swift.Collect;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.cluster.service.MasterService;
import com.fr.swift.container.NodeContainer;
import com.fr.swift.SwiftContext;
import com.fr.swift.heart.NodeState;
import com.fr.swift.heart.NodeType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rm.service.SwiftMasterService;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.List;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description 心跳超时检测
 * @since Advanced FineBI 5.0
 */
public class MasterHeartbeatCollect implements Collect {

    private MasterService masterService = SwiftContext.get().getBean(SwiftMasterService.class);

    private Thread thread;

    private final static long HEART_BEAT_TIME = 10000L;
    private final static long DELAY_TIME = 30000L;
    private final static long OFFLINE_TIME = 60000L;

    public MasterHeartbeatCollect() {
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
                        List<NodeState> allNodeStates = NodeContainer.getAllNodeStates();
                        boolean need2Sync = false;
                        for (NodeState nodeState : allNodeStates) {
                            long nowTime = System.currentTimeMillis();
                            long diffTime = nowTime - nodeState.getHeartBeatInfo().getHeartbeatTime().getTime();
                            NodeType originType = nodeState.getNodeType();
                            NodeType currentType;

                            if (diffTime > DELAY_TIME && diffTime <= OFFLINE_TIME) {
                                currentType = NodeType.DELAY;
                            } else if (diffTime > OFFLINE_TIME) {
                                currentType = NodeType.OFFLINE;
                                ClusterSwiftServerService.getInstance().offline(nodeState.getHeartBeatInfo().getAddress());
                            } else {
                                currentType = NodeType.ONLINE;
                                ClusterSwiftServerService.getInstance().online(nodeState.getHeartBeatInfo().getAddress());
                            }
                            if (originType != currentType) {
                                SwiftLoggers.getLogger().warn(nodeState.getHeartBeatInfo().getNodeName() + " is " + currentType);
                                nodeState.setNodeType(currentType);
                                need2Sync = true;
                            }
                        }
                        if (need2Sync) {
                            masterService.pushNodeStates();
                        }
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().error(e);
                    }
                    Thread.sleep(HEART_BEAT_TIME);
                }
            } catch (InterruptedException ite) {
                SwiftLoggers.getLogger().error(ite);
            }
        }
    }
}