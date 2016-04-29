package com.fr.bi.cluster.zookeeper.lock;

import com.fr.bi.cluster.wrapper.ZooKeeperWrapper;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * Created by Connery on 2015/4/15.
 */
public class BIFileLocker4test extends BIFileLock {
    public BIFileLocker4test(String path) {
        super(path);
    }

    @Override
    public ZooKeeperWrapper getZookeeper() {
        if (zooKeeper == null) {
            try {
                zooKeeper = ZookeeperTestUtil.generateZookeeper(10);
            } catch (Exception ex) {
                 BILogger.getLogger().error(ex.getMessage(), ex);
            }
        }
        return zooKeeper;
    }
}