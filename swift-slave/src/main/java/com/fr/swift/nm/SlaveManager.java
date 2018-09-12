package com.fr.swift.nm;

import com.fr.swift.Collect;
import com.fr.swift.cluster.manager.ClusterManager;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.nm.collector.SalveHeartBeatCollect;
import com.fr.swift.node.SwiftClusterNodeManager;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.AbstractSwiftManager;
import com.fr.swift.service.manager.ClusterServiceManager;
import com.fr.swift.util.ServiceBeanFactory;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service("slaveManager")
public class SlaveManager extends AbstractSwiftManager implements ClusterManager {

    private Collect heartBeatCollect = new SalveHeartBeatCollect();

    @Autowired
    private ClusterServiceManager clusterServiceManager;

    @Override
    public void startUp() throws Exception {
        lock.lock();
        try {
            if (!running) {
                if (ClusterSelector.getInstance().getFactory() instanceof SwiftClusterNodeManager) {
                    heartBeatCollect.startCollect();
                }
                super.startUp();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void shutDown() throws Exception {
        lock.lock();
        try {
            if (running) {
                if (ClusterSelector.getInstance().getFactory() instanceof SwiftClusterNodeManager) {
                    heartBeatCollect.stopCollect();
                }
                super.shutDown();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void installService() {
        try {
            clusterServiceManager.registerService(ServiceBeanFactory.getClusterSwiftServiceByNames(swiftProperty.getSwiftServiceNames()));
//            SlaveService slaveService = SwiftContext.get().getBean("swiftSlaveService", SwiftSlaveService.class);
//            slaveService.syncNodeStates();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    protected void uninstallService() {
        try {
            clusterServiceManager.unregisterService(ServiceBeanFactory.getClusterSwiftServiceByNames(swiftProperty.getSwiftServiceNames()));
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}
