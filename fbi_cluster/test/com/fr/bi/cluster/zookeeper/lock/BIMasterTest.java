package com.fr.bi.cluster.zookeeper.lock;

import com.fr.bi.cluster.wrapper.ZooKeeperWrapper;
import com.fr.bi.cluster.zookeeper.watcher.BIMaster;
import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

/**
 * Created by Connery on 2015/3/27.
 */
public class BIMasterTest extends TestCase {

    public void testMasterCompete() {
        final BIMaster candidate1 = new BIMaster();
        final BIMaster candidate2 = new BIMaster();
        try {
            final ZooKeeperWrapper zk1 = ZookeeperTestUtil.generateZookeeper(1000);
            final ZooKeeperWrapper zk2 = ZookeeperTestUtil.generateZookeeper(1000);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        candidate1.init(zk1);
                    } catch (Exception ex) {
                    }
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        candidate2.init(zk2);
                    } catch (Exception ex) {
                    }
                }
            }).start();
            try {
                Thread.sleep(10000);
            } catch (Exception ex) {
                 BILogger.getLogger().error(ex.getMessage(), ex);
            }
        } catch (Exception ex) {
        }
    }

}