package com.fr.bi.cluster.zookeeper.lock;

import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

/**
 * Created by Connery on 2015/3/26.
 */
public class ZooKeeperLockTest extends TestCase {
    private static String lock_name = "test1";
    private static String ip = "localhost";

    public void testLock() {


        final LockListener4Test lockListener4Test = new LockListener4Test();
        new Thread(new Runnable() {


            @Override
            public void run() {
                lockSimulator();
            }
        }).start();
        new Thread(new Runnable() {


            @Override
            public void run() {
                lockSimulator();
            }
        }).start();
        try {
            Thread.sleep(10000000);

        } catch (Exception ex) {

        }

    }

    private void lockSimulator() {
        BIFileLocker4test locker4test = new BIFileLocker4test("test");
        try {
            Object locker = new Object();
            BIFileLockWatcher watcher = new BIFileLockWatcher(locker);
            locker4test.lock(watcher);
            if (locker4test.isLocked()) {
                BILogger.getLogger().info(Thread.currentThread().getId());
                BILogger.getLogger().info(Thread.currentThread().getId() + " get the Lock");
                int i = 0;
                while (i++ < 10) {
                    BILogger.getLogger().info(Thread.currentThread().getId() + " do something:" + i);
                    Thread.sleep(1000);
                }
                locker4test.unlock();
            } else {

                synchronized (locker) {
                    BILogger.getLogger().info(Thread.currentThread().getName());
                    if (locker4test.checkExists()) {
                        BILogger.getLogger().info(Thread.currentThread().getId() + "Wait ");
                        locker.wait();
                    }
                    BILogger.getLogger().info(Thread.currentThread().getId() + "Wait end");
                }

            }
        } catch (Exception ex) {
            BILogger.getLogger().error(ex.getMessage(), ex);
        }
    }

}