package com.fr.bi.cluster.zookeeper;

import com.fr.base.FRContext;
import com.fr.bi.cluster.wrapper.ZooKeeperWrapper;
import com.fr.bi.cluster.zookeeper.exception.NotInitializeException;
import com.fr.bi.cluster.zookeeper.watcher.BIWorker;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.FRLogger;
import org.apache.zookeeper.KeeperException;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Connery on 2015/3/28.
 */
public class BIMissionDispatcher {
    private static final FRLogger LOG = FRContext.getLogger();
    private static BIMissionDispatcher instance = new BIMissionDispatcher();
    private Queue<ArrayList<BIWorkerNodeValue>> missions;
//    private static final Logger LOG = LoggerFactory.getLogger(BIClusterMissionManager.class);
    private ZooKeeperWrapper zk;
    private boolean isInitialized = false;

    private BIMissionDispatcher() {
        missions = new ConcurrentLinkedQueue<ArrayList<BIWorkerNodeValue>>();
    }

    public static BIMissionDispatcher getInstance() {
        return instance;
    }

    public void init(ZooKeeperWrapper zk) {
        this.zk = zk;
        isInitialized = true;
    }

    public void addMission(ArrayList<BIWorkerNodeValue> mission) {
        missions.add(mission);
    }

    public ArrayList<BIWorkerNodeValue> getMission() {
        return missions.poll();
    }

    public int getMissionSize() {
        return missions.size();
    }

    /**
     * 从队列中取出一个任务，分配到各个工作节点。
     * 并且返回当前任务。
     *
     * @return 当前分配的节点
     * @throws KeeperException
     * @throws InterruptedException
     * @throws NotInitializeException
     */
    public ArrayList<BIWorkerNodeValue> dispatcherMission() throws KeeperException, InterruptedException, NotInitializeException {
        if (isInitialized) {
            if (hasMission()) {
                List<String> children = new ArrayList<String>();
                try {
                    children = getWorkerPath();
                } catch (Exception ex) {
                     BILogger.getLogger().error(ex.getMessage(), ex);
                }
                if (!children.isEmpty()) {
                    ArrayList<BIWorkerNodeValue> currentMission = getMission();
                    if (currentMission.size() == children.size()) {
                        for (int i = 0; i < children.size(); i++) {
                            try {
                                zk.setData(BIWorker.PARENT_PATH + "/" + children.get(i), currentMission.get(i).toByte(), -1);
                            } catch (Exception ex) {
                                 BILogger.getLogger().error(ex.getMessage(), ex);
                            }
                        }
                    }
                    return currentMission;
                } else {
//                    LOG.error("所有的工作节点不可达");
                }
            }
        } else {
            throw new NotInitializeException();
        }
        return null;
    }

    private List<String> getWorkerPath() throws Exception {
        return zk.getChildren(BIWorker.PARENT_PATH, false);
    }

    public boolean hasMission() {
        return missions.size() > 0;
    }

}