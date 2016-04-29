package com.fr.bi.cluster.zookeeper;

import com.fr.base.FRContext;
import com.fr.bi.cluster.wrapper.ZooKeeperWrapper;
import com.fr.bi.cluster.zookeeper.exception.MissionNotStopException;
import com.fr.bi.cluster.zookeeper.watcher.BIWorker;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.FRLogger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Connery on 2015/3/28.
 * <p/>
 * 负责监视/Worker路径每个工作当前的任务完成情况。
 * 开始一个新任务，首先必须当前的任务的id
 * 当任意工作节点改变工作目录的时候，监视器会查看所有工作节点
 * 是否当前任务，已经完成。
 */
public class BIMissionMonitor implements Watcher {
    private static final FRLogger LOG = FRContext.getLogger();
    private static int DEFAULT_ID = -1;
    private long missionID;
    private boolean notMonitorMission = true;
    private ZooKeeperWrapper zk;
//    private static final Logger LOG = LoggerFactory.getLogger(BIClusterMissionManager.class);
    private ArrayList<BIMissionListener> listeners;

    /**
     * 初始化函数
     * 确保worker路径下有工作节点，才能初始化。
     * 否则，watchTheWorker无效。
     *
     * @param zk
     * @param listener
     */
    public void init(ZooKeeperWrapper zk, BIMissionListener listener) {
        this.zk = zk;
        this.listeners = new ArrayList<BIMissionListener>();
        try {
            watchTheWorker();
        } catch (Exception ex) {

        }

    }

    public void registerListener(BIMissionListener listener) {
        if (!this.listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void monitorNewMission(long missionID) throws MissionNotStopException {
        if (notMonitorMission) {
//            LOG.info(this.missionID + "号任务，所有节点全部完成，被分配了：" + missionID + "号任务");
            this.missionID = missionID;
            notMonitorMission = false;

        } else {
//            LOG.error(this.missionID + "任务没有完成，但是却被分配了：" + missionID + "号任务，已终止该操作");
            throw new MissionNotStopException();
        }
    }

    private void finishTheMission() {
        notMonitorMission = true;
    }

    public boolean isNotMonitorMission() {
        return notMonitorMission;
    }

    private void fireListener() {
        if (listeners != null) {
            Iterator<BIMissionListener> it = listeners.iterator();
            while (it.hasNext()) {
//                LOG.info(this.missionID + "轮，所有节点全部完成,通知其监听");
                it.next().fireJobFinishedListener();
            }
        }
    }


    @Override
    public void process(WatchedEvent event) {
        try {
//            LOG.info("路径：" + event.getPath() + "促发了monitor,事件类型：" + event.getType());
            if (checkStatus()) {
                finishTheMission();
                fireListener();
            }

        } catch (Exception ex) {
             BILogger.getLogger().error(ex.getMessage(), ex);
        }
    }

    /**
     * 检查全部工作节点，是否完成工作
     *
     * @return 如果全部完成，返回true；
     */
    private boolean checkStatus() throws Exception {
        Iterator<String> it = zk.getChildren(BIWorker.PARENT_PATH, false).iterator();
        while (it.hasNext()) {
            String worker = it.next();
            byte[] value = zk.getData(BIWorker.PARENT_PATH + "/" + worker, this, null);
            BIWorkerNodeValue nodeValue = new BIWorkerNodeValue();
            nodeValue.init(value);
//            LOG.info(worker + "号worker，当前任务状态为：" + nodeValue.getStatus());
            if (nodeValue.getMissionID() == missionID) {
                if (nodeValue.getStatus() != BIWorkerNodeValue.STATUS_FINISH) {
//                    LOG.info(worker + "号worker，没有完成，返回False");
                    return false;
                }
            } else {
//                LOG.info(worker + "号worker的任务ID为" + nodeValue.getMissionID() + "，与当前任务号不符合，返回Fasle.当前任务号为：" + missionID);
                return false;
            }
        }
//        LOG.info("各个节点任务全部完成");
        return true;
    }

    /**
     * TODO 监听前，判断当前每个worker节点的任务状态都为：结束
     *
     * @throws KeeperException
     * @throws InterruptedException
     */
    private void watchTheWorker() throws Exception {
//        LOG.info("注册监听worker节点");
        List<String> children = zk.getChildren(BIWorker.PARENT_PATH, false);
        Iterator<String> it = children.iterator();
//        LOG.info("当前worker节点有：" + children.size());
        while (it.hasNext()) {
            String worker = it.next();
            zk.exists(BIWorker.PARENT_PATH + "/" + worker, this);
        }
    }
}