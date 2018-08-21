package com.fr.swift.service.register;

import com.fr.swift.ClusterNodeService;
import com.fr.swift.annotation.ClusterService;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.nm.SlaveManager;
import com.fr.swift.rm.MasterManager;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.manager.ClusterServiceManager;
import com.fr.swift.util.ServiceBeanFactory;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.beans.factory.annotation.Qualifier;
import com.fr.third.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Map;

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
    @Qualifier("masterManager")
    private MasterManager masterManager;

    @Autowired
    @Qualifier("slaveManager")
    private SlaveManager slaveManager;

    private ClusterSwiftRegister() {
    }

    @Override
    public void serviceRegister() throws Exception {
        initClusterService();
        if (ClusterSelector.getInstance().getFactory().isMaster()) {
            LOGGER.info("=====Cluster master!=====");
            ClusterSwiftServerService.getInstance().start();
            String masterAddress = swiftProperty.getMasterAddress();
            serviceInfoService.saveOrUpdate(new SwiftServiceInfoBean(ClusterNodeService.SERVICE, masterAddress, masterAddress, true));
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
            masterManager.shutDown();
            slaveManager.shutDown();
            clusterServiceManager.unregisterService(ServiceBeanFactory.getClusterSwiftServiceByNames(swiftProperty.getSwiftServiceNames()));
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    /**
     * 目前加载zk形式的集群选举插件。
     */
    private void initClusterService() {
        Map<String, Object> map = SwiftContext.get().getBeansWithAnnotation(ClusterService.class);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            try {
                String initMethodName = entry.getValue().getClass().getAnnotation(ClusterService.class).initMethod();
                Method method = entry.getValue().getClass().getMethod(initMethodName);
                method.invoke(entry.getValue());
            } catch (Exception e) {
                LOGGER.error(e);
                continue;
            }
        }
        return;
    }
}
