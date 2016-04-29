package com.fr.bi.cluster.zookeeper.lock;

import com.fr.bi.cluster.wrapper.ZooKeeperWrapper;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * Created by Connery on 2015/4/14.
 */
public class ZooKeeperLock4Test extends ZooKeeperLock {
    public ZooKeeperLock4Test(String lockName) {
        super(lockName);
    }

    private ZooKeeperWrapper getZooKeeper() {
        if (zooKeeper == null) {
            try {
                zooKeeper = ZookeeperTestUtil.generateZookeeper(1);

            } catch (Exception ex) {
                 BILogger.getLogger().error(ex.getMessage(), ex);
            }
        }
        return zooKeeper;
    }
}