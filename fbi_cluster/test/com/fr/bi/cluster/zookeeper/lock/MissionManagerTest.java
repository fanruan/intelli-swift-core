package com.fr.bi.cluster.zookeeper.lock;

import com.fr.bi.cluster.wrapper.ZooKeeperWrapper;
import com.fr.bi.cluster.zookeeper.BIClusterMissionManager;
import com.fr.bi.cluster.zookeeper.watcher.BIMaster;
import com.fr.bi.cluster.zookeeper.watcher.BIWorker;
import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

/**
 * Created by Connery on 2015/3/30.
 */
public class MissionManagerTest extends TestCase {
    public void testMision() {
        try {
            ZooKeeperWrapper zooKeeper = ZookeeperTestUtil.generateZookeeper(100000);
            BIWorker worker;
            BIMaster master;
            BIClusterMissionManager manager = new BIClusterMissionManager();
            master = new BIMaster();
            worker = new BIWork4Test();
            worker.init(zooKeeper);
            master.init(zooKeeper);
            manager.init(worker, master);
//            manager.prepareMissions(BIWorkerNodeValue.TASK_LOAD_DB, TableKeyGenerator.generator());
//            ArrayList<Object> tableKeys = manager.getMissions();
//            System.out.println(tableKeys.size());
        } catch (Exception ex) {
             BILogger.getLogger().error(ex.getMessage(), ex);
        }
    }

}