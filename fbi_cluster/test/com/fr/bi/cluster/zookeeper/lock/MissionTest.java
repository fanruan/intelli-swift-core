package com.fr.bi.cluster.zookeeper.lock;

import com.fr.base.FRContext;
import com.fr.bi.cluster.wrapper.ZooKeeperWrapper;
import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

/**
 * Created by Connery on 2015/3/29.
 */
public class MissionTest extends TestCase {
    public void testMission() {
        try {
            final ZooKeeperWrapper zk = ZookeeperTestUtil.generateZookeeper(100);
            final SimulatorMachine machine = new SimulatorMachine();
            final SimulatorMachine machine1 = new SimulatorMachine();
            final SimulatorMachine machine2 = new SimulatorMachine();
            FRContext.getLogger();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        machine.init(zk);
                    } catch (Exception ex) {
                        BILogger.getLogger().error(ex.getMessage(),ex);
                    }
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        machine1.init(zk);
                    } catch (Exception ex) {
                        BILogger.getLogger().error(ex.getMessage(), ex);

                    }
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        machine2.init(zk);
                    } catch (Exception ex) {
                        BILogger.getLogger().error(ex.getMessage(), ex);


                    }
                }
            }).start();

            Thread.sleep(1000000);

        } catch (Exception ex) {
        }
    }
}