package com.fr.swift.service.register;

import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.manager.ClusterServiceManager;
import com.fr.swift.util.ServiceBeanFactory;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

/**
 * This class created on 2018/8/13
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service("frClusterSwiftRegister")
public class FRClusterSwiftRegister extends AbstractSwiftRegister {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger();

    @Autowired
    private SwiftServiceInfoService serviceInfoService;

    @Autowired
    private ClusterServiceManager clusterServiceManager;

    @Override
    public void serviceRegister() throws Exception {
        if (ClusterSelector.getInstance().getFactory().isMaster()) {
            LOGGER.info("=====Cluster master!=====");
            ClusterSwiftServerService.getInstance().start();

        } else {
            LOGGER.info("=====Cluster slaver!=====");
            clusterServiceManager.registerService(ServiceBeanFactory.getClusterSwiftServiceByNames(swiftProperty.getSwiftServiceNames()));
        }
    }

    @Override
    public void serviceUnregister() throws Exception {
        clusterServiceManager.unregisterService(ServiceBeanFactory.getClusterSwiftServiceByNames(swiftProperty.getSwiftServiceNames()));
    }
}
