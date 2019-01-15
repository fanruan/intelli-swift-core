package com.fr.swift.nm;

import com.fr.swift.Collect;
import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.nm.collector.SlaveHeartBeatCollect;
import com.fr.swift.node.SwiftClusterNodeManager;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.AbstractSwiftManager;
import com.fr.swift.service.SwiftManager;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.local.ServiceManager;
import com.fr.swift.service.manager.ClusterServiceManager;
import com.fr.swift.util.ServiceBeanFactory;

import java.util.List;

/**
 * This class created on 2018/7/17
 *
 * @author Lucifer
 * @description 控制集群情况下，service启动和service register和unregister，slave需要向远程master注册和注销。
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "slaveManager")
public class SlaveManager extends AbstractSwiftManager implements SwiftManager {

    private Collect heartBeatCollect = new SlaveHeartBeatCollect();

    private ServiceManager serviceManager = SwiftContext.get().getBean(ServiceManager.class);

    private ClusterServiceManager clusterServiceManager = SwiftContext.get().getBean(ClusterServiceManager.class);

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
            serviceManager.startUp();
            List<SwiftService> swiftServices = ServiceBeanFactory.getSwiftServiceByNames(swiftProperty.getSwiftServiceNames());
            clusterServiceManager.registerService(swiftServices);
//            SlaveService slaveService = SwiftContext.get().getBean("swiftSlaveService", SwiftSlaveService.class);
//            slaveService.syncNodeStates();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    protected void uninstallService() {
        try {
            List<SwiftService> swiftServices = ServiceBeanFactory.getSwiftServiceByNames(swiftProperty.getSwiftServiceNames());
            clusterServiceManager.unregisterService(swiftServices);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }
}
