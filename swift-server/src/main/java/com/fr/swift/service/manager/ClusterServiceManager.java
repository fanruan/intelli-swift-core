package com.fr.swift.service.manager;

import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.util.List;

/**
 * This class created on 2018/8/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service("clusterServiceManager")
public class ClusterServiceManager extends AbstractServiceManager<SwiftService> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger();

    @Autowired
    private SwiftProperty swiftProperty;

    @Autowired
    private SwiftServiceInfoService serviceInfoService;

    private SwiftServiceListenerHandler senderProxy;

    private SwiftServiceInfoBean swiftServiceInfoBean;

    private ClusterServiceManager() {

    }

    @Override
    public void registerService(List<SwiftService> swiftServiceList) throws SwiftServiceException {
        lock.lock();
        try {
            refreshInfo();
            for (SwiftService swiftService : swiftServiceList) {
                swiftService.setId(swiftProperty.getServerAddress());
                LOGGER.debug("begin to register " + swiftService.getServiceType() + " to " + swiftServiceInfoBean.getClusterId() + "!");
                senderProxy.registerService(swiftService);
                swiftService.start();
                LOGGER.debug("register " + swiftService.getServiceType() + " to " + swiftServiceInfoBean.getClusterId() + " succeed!");
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void unregisterService(List<SwiftService> swiftServiceList) throws SwiftServiceException {
        lock.lock();
        try {
            for (SwiftService swiftService : swiftServiceList) {
                swiftService.setId(swiftProperty.getServerAddress());
                LOGGER.debug("begain to unregister " + swiftService.getServiceType() + " from " + swiftServiceInfoBean.getClusterId() + "!");
                senderProxy.unRegisterService(swiftService);
                swiftService.shutdown();
                LOGGER.debug("unregister " + swiftService.getServiceType() + " from " + swiftServiceInfoBean.getClusterId() + " succeed!");
            }
        } finally {
            lock.unlock();
        }
    }

    private void refreshInfo() {
        ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
//        RemoteServiceSender remoteServiceSender = RemoteServiceSender.getInstance();
//        RemoteServiceSender remoteServiceSender = SwiftContext.get().getBean(RemoteServiceSender.class);
//        List<SwiftServiceInfoBean> swiftServiceInfoBeans = serviceInfoService.getServiceInfoByService(ClusterNodeService.SERVICE);
//        swiftServiceInfoBean = swiftServiceInfoBeans.get(0);
//        URL url = UrlSelector.getInstance().getFactory().getURL(swiftServiceInfoBean.getServiceInfo());
//        senderProxy = proxyFactory.getProxy(remoteServiceSender, SwiftServiceListenerHandler.class, url);
        senderProxy = proxyFactory.getProxy(SwiftServiceListenerHandler.class);
    }
}
