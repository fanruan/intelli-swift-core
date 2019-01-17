package com.fr.swift.rm;

import com.fr.swift.ClusterNodeService;
import com.fr.swift.Collect;
import com.fr.swift.cluster.manager.ClusterManager;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rm.collector.MasterHeartbeatCollect;
import com.fr.swift.rm.collector.MasterSynchronizer;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.AbstractSwiftManager;
import com.fr.swift.service.CollateExecutor;
import com.fr.swift.service.SwiftService;
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
@Service("masterManager")
public class MasterManager extends AbstractSwiftManager implements ClusterManager {

    @Autowired
    private SwiftServiceInfoService serviceInfoService;

    @Autowired
    private ClusterServiceManager clusterServiceManager;

    @Autowired
    private CollateExecutor collateExecutor;

    private Collect heartBeatCollect = new MasterHeartbeatCollect();


    private MasterSynchronizer masterSyncRunnable = new MasterSynchronizer();

    @Override
    public void startUp() throws Exception {
        lock.lock();
        try {
            if (!running) {
                ClusterSwiftServerService.getInstance().start();
                heartBeatCollect.startCollect();
                masterSyncRunnable.start();
                collateExecutor.start();
                String masterAddress = swiftProperty.getMasterAddress();
                serviceInfoService.saveOrUpdate(new SwiftServiceInfoBean(ClusterNodeService.SERVICE, masterAddress, masterAddress, true));
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
                heartBeatCollect.stopCollect();
                masterSyncRunnable.stop();
                collateExecutor.stop();
                super.shutDown();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void installService() {
        try {
            for (SwiftService swiftService : ServiceBeanFactory.getClusterSwiftServiceByNames(swiftProperty.getSwiftServiceNames())) {
                swiftService.setId(ClusterSelector.getInstance().getFactory().getCurrentId());
                swiftService.start();
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    protected void uninstallService() {

    }
}
