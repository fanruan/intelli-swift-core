package com.fr.bi.cluster.zookeeper.watcher;

import com.fr.base.FRContext;
import com.fr.bi.cluster.wrapper.ZooKeeperWrapper;
import com.fr.bi.cluster.zookeeper.BIWatcher;
import com.fr.bi.cluster.zookeeper.BIWorkerNodeValue;
import com.fr.bi.cluster.zookeeper.operation.BIWorkerBasicIndexOperation;
import com.fr.bi.cluster.zookeeper.operation.BIWorkerDBOperation;
import com.fr.bi.cluster.zookeeper.operation.BIWorkerFirstIndexOperation;
import com.fr.bi.cluster.zookeeper.operation.BIWorkerOperation;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.FRLogger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.List;


/**
 * Created by Connery on 2015/3/27.
 * 负责从Master获得任务
 */
public class BIWorker extends BIWatcher {
    public static final String PARENT_PATH = "/worker";
    private static final String CHILD_FOLD = "job";
    private static final FRLogger LOG = FRContext.getLogger();
    //    private static final Logger LOG = LoggerFactory.getLogger(BIWorker.class);
    private BIWorkerNodeValue lastValue;
    private BIWorkerNodeValue currentValue;
    private String id;
    private long sessionId;

    @Override
    public String getFocusedEventPath() {
        return id;
    }

    private String getPrefix() {
        return CHILD_FOLD + "-" + sessionId;
    }

    @Override
    public void eventProcessor(WatchedEvent event) {
        synchronized (this) {
            BIWorkerNodeValue nodeValue = getCurrentValue();
//            LOG.info("Event的状态：" + event.getState().toString());
            if (event.getType() == Event.EventType.NodeDataChanged
                    && nodeValue.getStatus() != BIWorkerNodeValue.STATUS_FINISH) {
                lastValue = nodeValue;
                try {
                    nodeValue = operate(nodeValue);
                    zk.setData(getFocusedEventPath(), nodeValue.toByte(), -1);
                } catch (Exception ex) {
                     BILogger.getLogger().error(ex.getMessage(), ex);
                }
            }
        }
    }

    private BIWorkerNodeValue operate(BIWorkerNodeValue nodeValue) {
        BIWorkerOperation operation = new BIWorkerDBOperation();
        switch (nodeValue.getTaskName()) {
            case BIWorkerNodeValue.TASK_LOAD_DB:
//                LOG.info("正在处理数据载入");
                operation = new BIWorkerDBOperation();
                break;
            case BIWorkerNodeValue.TASK_BASIC_INDEX:
//                LOG.info("正在处理关联索引生成工作");

                operation = new BIWorkerBasicIndexOperation();
                break;
            case BIWorkerNodeValue.TASK_FIRST_INDEX:
//                LOG.info("正在处理基础索引生成工作");

                operation = new BIWorkerFirstIndexOperation();
                break;
            default:
//                LOG.error("检查当前任务");
        }
        return operation.operate(nodeValue);
    }

    protected void dealTask(BIWorkerNodeValue biWorkerTask) {

    }

    public BIWorkerNodeValue getCurrentValue() {
        BIWorkerNodeValue task = new BIWorkerNodeValue();
        try {
            byte[] content = zk.getData(getFocusedEventPath(), this, null);
            task.init(content);
        } catch (Exception ex) {
//            LOG.error("Worker:" + getPrefix() + "");
             BILogger.getLogger().error(ex.getMessage(), ex);
        }
        return task;
    }

    @Override
    public void init(ZooKeeperWrapper zk) throws Exception {
        super.init(zk);
        sessionId = zk.getSessionId();
    }

    @Override
    public void ensureNodePathExists() throws Exception {
        Stat s = zk.exists(PARENT_PATH, this);
        if (s == null) {
            try {
                zk.create(PARENT_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (Exception ex) {
                 BILogger.getLogger().error(ex.getMessage(), ex);
//                LOG.error("Zookeeper已经存在" + PARENT_PATH + "路径，报错信息：" + ex.toString());
            }
        }
        id = zk.create(PARENT_PATH + "/" + getPrefix(), new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
//        LOG.info("zookeeper:" + getPrefix() + ",当前WorkerID为：" + id);
    }

    public List<String> getWorkers() throws Exception {
        return zk.getChildren(BIWorker.PARENT_PATH, false);
    }

}