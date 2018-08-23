package com.fr.swift.rm;

import com.fr.swift.ClusterNodeService;
import com.fr.swift.Collect;
import com.fr.swift.cluster.manager.ClusterManager;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.rm.collector.MasterHeartbeatCollect;
import com.fr.swift.service.AbstractSwiftManager;
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

    private Collect heartBeatCollect = new MasterHeartbeatCollect();

    @Override
    public void startUp() throws Exception {
        lock.lock();
        try {
            if (!running) {
                ClusterSwiftServerService.getInstance().start();
                heartBeatCollect.startCollect();
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
                super.shutDown();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void installService() {

    }

    @Override
    protected void uninstallService() {

    }
}
