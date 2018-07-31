package com.fr.swift.nm;

import com.fr.swift.Collect;
import com.fr.swift.cluster.manager.AbstractClusterManager;
import com.fr.swift.cluster.service.SlaveService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.nm.collector.SalveHeartBeatCollect;
import com.fr.swift.nm.service.SwiftSlaveService;
import com.fr.third.springframework.stereotype.Service;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service("slaveManager")
public class SlaveManager extends AbstractClusterManager {

    private Collect heartBeatCollect = new SalveHeartBeatCollect();

    public void startUp() {
        if (!isRunning) {
            heartBeatCollect.startCollect();
            super.startUp();
        }
    }

    public void shutDown() {
        if (isRunning) {
            heartBeatCollect.stopCollect();
            super.shutDown();
        }
    }

    @Override
    protected void installService() {
        try {
            SlaveService slaveService = SwiftContext.get().getBean("swiftSlaveService", SwiftSlaveService.class);
            slaveService.syncNodeStates();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    protected void uninstallService() {

    }
}
