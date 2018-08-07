package com.fr.swift.rm;

import com.fr.swift.Collect;
import com.fr.swift.cluster.manager.AbstractClusterManager;
import com.fr.swift.rm.collector.MasterHeartbeatCollect;
import com.fr.third.springframework.stereotype.Service;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service("masterManager")
public class MasterManager extends AbstractClusterManager {

    private Collect heartBeatCollect = new MasterHeartbeatCollect();

    public synchronized void startUp() {
        if (!isRunning) {
            heartBeatCollect.startCollect();
            super.startUp();
        }
    }

    public synchronized void shutDown() {
        if (isRunning) {
            heartBeatCollect.stopCollect();
            super.shutDown();
        }
    }

    protected void installService() {

    }

    protected void uninstallService() {

    }
}
