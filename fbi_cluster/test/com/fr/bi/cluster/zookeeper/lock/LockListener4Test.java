package com.fr.bi.cluster.zookeeper.lock;

import com.fr.bi.stable.utils.code.BILogger;

/**
 * Created by Connery on 2015/3/26.
 */
public class LockListener4Test implements LockListener {
    public ZooKeeperLock zooKeeperLock;

    @Override
    public void lockAcquired() {
        new Thread(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                while (i++ < 11) {

                }
                try {
//                    Thread.sleep(10000);
                } catch (Exception ex) {
                     BILogger.getLogger().error(ex.getMessage(), ex);
                }
                zooKeeperLock.unlock();
            }
        }).start();
    }

    @Override
    public void lockReleased() {

    }
}