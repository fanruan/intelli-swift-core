package com.fr.bi.cluster.zookeeper.lock;

import com.fr.bi.cluster.wrapper.ZooKeeperWrapper;
import com.fr.bi.cluster.zookeeper.BIMasterController;
import com.fr.bi.cluster.zookeeper.watcher.BIMaster;
import com.fr.bi.cluster.zookeeper.watcher.BIWorker;

/**
 * Created by Connery on 2015/3/29.
 */
public class SimulatorMachine {
    private BIWorker worker;
    private BIMaster master;
    private BIMasterController controller;

    public void init(ZooKeeperWrapper zooKeeper) {
        master = new BIMaster();
        try {
            worker = new BIWork4Test();
            worker.init(zooKeeper);

        } catch (Exception ex) {

        }
        try {

            master.init(zooKeeper);
        } catch (Exception ex) {

        }
        if (master.isMaster()) {
//            System.out.println("成为Master");
            controller = master.getController();
//            controller.addMission(new SimulatorMissionGenerator().generation());
//            controller.addMission(new SimulatorMissionGenerator().generation_diff());
//            controller.addMission(new SimulatorMissionGenerator().generation_same());
        }


    }
}