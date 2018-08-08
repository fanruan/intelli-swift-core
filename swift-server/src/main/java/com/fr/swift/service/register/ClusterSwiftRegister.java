package com.fr.swift.service.register;

import com.fr.swift.cluster.manager.ClusterManager;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.core.cluster.SwiftClusterService;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.ClusterSwiftServerService;
import com.fr.swift.service.manager.ServiceManager;
import com.fr.third.springframework.stereotype.Service;

/**
 * This class created on 2018/6/4
 *
 * @author Lucifer
 * @description todo 注册和注销service的动作均移动到master和slave的service里去做。service隔离通信层
 * @since Advanced FineBI 5.0
 */
@Service("clusterSwiftRegister")
public class ClusterSwiftRegister extends AbstractSwiftRegister {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger();

    private SwiftServiceInfoService serviceInfoService;

    private ClusterSwiftRegister() {
        serviceInfoService = SwiftContext.get().getBean(SwiftServiceInfoService.class);
    }

    @Override
    public void serviceRegister() throws Exception {
        if (ClusterSelector.getInstance().getFactory().isMaster()) {
            LOGGER.info("=====Cluster master!=====");
            ClusterSwiftServerService.getInstance().start();
            String masterAddress = SwiftContext.get().getBean("swiftProperty", SwiftProperty.class).getMasterAddress();
            serviceInfoService.saveOrUpdate(new SwiftServiceInfoBean(SwiftClusterService.SERVICE, masterAddress, masterAddress, true));
            SwiftContext.get().getBean("masterManager", ClusterManager.class).startUp();
        } else {
            LOGGER.info("=====Cluster slaver!=====");
            ServiceManager serviceManager = SwiftContext.get().getBean("clusterServiceManager", ServiceManager.class);
            SwiftProperty swiftProperty = SwiftContext.get().getBean(SwiftProperty.class);
            serviceManager.registerService(swiftProperty.getSwiftServiceList());
            SwiftContext.get().getBean("slaveManager", ClusterManager.class).startUp();
        }
    }

    @Override
    public void serviceUnregister() {
        try {
            if (ClusterSelector.getInstance().getFactory().isMaster()) {
                SwiftContext.get().getBean("masterManager", ClusterManager.class).shutDown();
            } else {
                SwiftContext.get().getBean("slaveManager", ClusterManager.class).shutDown();
            }
            ServiceManager serviceManager = SwiftContext.get().getBean("clusterServiceManager", ServiceManager.class);
            SwiftProperty swiftProperty = SwiftContext.get().getBean(SwiftProperty.class);
            serviceManager.unregisterService(swiftProperty.getSwiftServiceList());
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }
}
