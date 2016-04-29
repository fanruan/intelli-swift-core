package com.fr.bi.cluster.zookeeper.watcher;

import com.fr.base.FRContext;
import com.fr.bi.cluster.zookeeper.*;
import com.fr.bi.cluster.zookeeper.lock.WriteLock;
import com.fr.bi.cluster.zookeeper.operation.BIMasterBasicIndexOperation;
import com.fr.bi.cluster.zookeeper.operation.BIMasterDBOperation;
import com.fr.bi.cluster.zookeeper.operation.BIMasterFirstIndexOperation;
import com.fr.bi.cluster.zookeeper.operation.BIMasterOperation;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.FRLogger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Connery on 2015/3/27.
 */
public class BIMaster extends BIWatcher implements BIMissionListener {

    private static final String PARENT_PATH = "/Master";
    private static final FRLogger LOG = FRContext.getLogger();
    //    private static final Logger LOG = LoggerFactory.getLogger(BIMaster.class);
    private String id;
    private boolean isMaster;
    private Boolean competed = false;
    private BIMasterController controller;
    private boolean available = true;
    private Queue<Integer> preparations = new LinkedList<Integer>();
    private long userId = -1;
    private long beginTime;

    private String basePath;
    private String tmpPath;


    @Override
    public String getFocusedEventPath() {
        return id;
    }

    @Override
    public void eventProcessor(WatchedEvent event) {

    }

    @Override
    public void fireJobFinishedListener() {
        if (userId == -1) {
//            LOG.error("Master节点没有userId");

        } else {

            executeNextPreparation(basePath, tmpPath, userId);

        }
    }

    public BIMasterController getController() {
        return controller;
    }

    @Override
    public void ensureNodePathExists() throws Exception {

        Stat s = zk.exists(PARENT_PATH, false);
        if (s == null) {
            try {
                zk.create(PARENT_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (Exception ex) {
                 BILogger.getLogger().error(ex.getMessage(), ex);
//                LOG.error("Zookeeper已经存在" + PARENT_PATH + "路径，报错信息：" + ex.toString());
            }
        }
        competeMaster();
    }

    /**
     * 通过WriterLock来选出唯一的Master
     */
    private void competeMaster() {
        synchronized (competed) {

            WriteLock writeLock = new WriteLock(zk.getZookeeperHandler().getZooKeeper(), PARENT_PATH, ZooKeeperConstant.DEFAULT_ACL);

            boolean isLocked = false;
            try {
                isLocked = writeLock.lock();
                id = writeLock.getId();
                competed.notify();
                competed = true;
            } catch (InterruptedException ex) {
                 BILogger.getLogger().error(ex.getMessage(), ex);
            } catch (KeeperException ex) {
                 BILogger.getLogger().error(ex.getMessage(), ex);
            } catch (Exception ex) {
                 BILogger.getLogger().error(ex.getMessage(), ex);
            }

            if (isLocked) {
                isMaster = true;
                startMasterController();
            } else {
                isMaster = false;
                try {
                    zk.delete(id, -1);
                } catch (Exception ex) {
                     BILogger.getLogger().error(ex.getMessage(), ex);
                }
            }

        }
    }

    public boolean isMaster() {
        synchronized (competed) {
            if (!competed) {
                try {
                    competed.wait();
                } catch (InterruptedException ex) {

                }
            }
            return isMaster;
        }
    }

    private void startMasterController() {
        if (isMaster()) {
            controller = new BIMasterController(zk);
            try {
                controller.startWork();
                controller.registerMissionListener(this);
            } catch (Exception ex) {
                 BILogger.getLogger().error(ex.getMessage(), ex);
            }
        }
    }


    public void startBuildCube(String basePath, String tmpPath, long userId) {
        synchronized (this) {
            if (available) {
                available = true;
                beginTime = System.currentTimeMillis();
                if (preparations.isEmpty()) {
                    fullTask();
                }
                this.userId = userId;

                this.basePath = basePath;
                this.tmpPath = tmpPath;
                int preparation = this.preparations.poll();

                if (preparation == BIWorkerNodeValue.TASK_BEGIN) {

                    executeNextPreparation(basePath, tmpPath, userId);

                }
            }
        }
    }


    private void executeNextPreparation(String basePath, String tmpPath, long userId) {

        int preparation = getNextPreparation();
        if (preparation != -1) {

            prepare(basePath, tmpPath, preparation, userId);

        }
    }

    private int getNextPreparation() {
        if (!preparations.isEmpty()) {
            return preparations.poll();
        } else {
            return -1;
        }
    }


    private void prepare(String basePath, String tmpPath, int taskName, long userId) {

        BIMasterOperation operation = null;
        switch (taskName) {
            case BIWorkerNodeValue.TASK_LOAD_DB:
//                LOG.info("载入数据库准备工作正在进行");
                operation = new BIMasterDBOperation();
                break;
            case BIWorkerNodeValue.TASK_FIRST_INDEX:
//                LOG.info("关联索引准备工作正在进行");
                operation = new BIMasterFirstIndexOperation();
                break;
            case BIWorkerNodeValue.TASK_BASIC_INDEX:
//                LOG.info("基础索引准备工作正在进行");
                operation = new BIMasterBasicIndexOperation();
                this.available = true;
//                LOG.info("当前任务周期结束");
//                LOG.info("分布式生成cube, 一共耗时:" + DateUtils.timeCostFrom(this.beginTime));
                break;
            default:
//                LOG.error("检查当前任务");
        }
        if (operation != null) {

            operation.prepare(basePath, tmpPath, userId);

        }
    }

    private void fullTask() {
        synchronized (this) {
            if (this.preparations.isEmpty()) {
                this.preparations.add(BIWorkerNodeValue.TASK_BEGIN);
                this.preparations.add(BIWorkerNodeValue.TASK_LOAD_DB);
                this.preparations.add(BIWorkerNodeValue.TASK_FIRST_INDEX);
                this.preparations.add(BIWorkerNodeValue.TASK_BASIC_INDEX);
//            this.preparations.add(BIWorkerNodeValue.TASK_END);
            } else {
//                LOG.error("当前准备事项并未结束，无法重新添加任务");
            }
        }
    }

}