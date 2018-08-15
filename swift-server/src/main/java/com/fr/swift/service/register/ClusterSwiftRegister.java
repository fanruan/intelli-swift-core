package com.fr.swift.service.register;

import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.core.cluster.SwiftClusterService;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.nm.SlaveManager;
import com.fr.swift.rm.MasterManager;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.manager.ClusterServiceManager;
import com.fr.swift.util.ServiceBeanFactory;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

/**
 * This class created on 2018/6/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service("clusterSwiftRegister")
public class ClusterSwiftRegister extends AbstractSwiftRegister {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger();

    @Autowired
    private SwiftServiceInfoService serviceInfoService;

    @Autowired
    private ClusterServiceManager clusterServiceManager;

    @Autowired
    private MasterManager masterManager;

    @Autowired
    private SlaveManager slaveManager;

    private ClusterSwiftRegister() {
    }

    @Override
    public void serviceRegister() throws Exception {
        if (ClusterSelector.getInstance().getFactory().isMaster()) {
            LOGGER.info("=====Cluster master!=====");
            ClusterSwiftServerService.getInstance().start();
            String masterAddress = swiftProperty.getMasterAddress();
            serviceInfoService.saveOrUpdate(new SwiftServiceInfoBean(SwiftClusterService.SERVICE, masterAddress, masterAddress, true));
            masterManager.startUp();
        } else {
            LOGGER.info("=====Cluster slaver!=====");
            clusterServiceManager.registerService(ServiceBeanFactory.getClusterSwiftServiceByNames(swiftProperty.getSwiftServiceNames()));
            slaveManager.startUp();
        }
    }

    @Override
    public void serviceUnregister() {
        try {
            if (ClusterSelector.getInstance().getFactory().isMaster()) {
                masterManager.shutDown();
            } else {
                slaveManager.shutDown();
            }
            clusterServiceManager.unregisterService(ServiceBeanFactory.getClusterSwiftServiceByNames(swiftProperty.getSwiftServiceNames()));
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }
}
