package com.fr.swift.service.register;

import com.fr.swift.ProxyFactory;
import com.fr.swift.URL;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.frrpc.SwiftClusterService;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.selector.UrlSelector;
import com.fr.swift.service.SwiftAnalyseService;
import com.fr.swift.service.SwiftHistoryService;
import com.fr.swift.service.SwiftIndexingService;
import com.fr.swift.service.SwiftRealtimeService;
import com.fr.swift.service.SwiftRegister;
import com.fr.swift.service.listener.RemoteServiceSender;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;

import java.util.List;

/**
 * This class created on 2018/6/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class AbstractSwiftRegister implements SwiftRegister {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(AbstractSwiftRegister.class);

    private SwiftServiceInfoService serviceInfoService;

    public AbstractSwiftRegister() {
        serviceInfoService = SwiftContext.getInstance().getBean(SwiftServiceInfoService.class);
    }

    protected void localServiceRegister() throws SwiftServiceException {
        SwiftAnalyseService.getInstance().start();
        SwiftHistoryService.getInstance().start();
        SwiftIndexingService.getInstance().start();
        SwiftRealtimeService.getInstance().start();
    }

    protected void masterLocalServiceRegister() {
        String masterAddress = SwiftContext.getInstance().getBean("swiftProperty", SwiftProperty.class).getMasterAddress();
        serviceInfoService.saveOrUpdateServiceInfo(new SwiftServiceInfoBean(SwiftClusterService.SERVICE, masterAddress, masterAddress, true));
    }

    protected void remoteServiceRegister() {
        ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();

        RemoteServiceSender remoteServiceSender = RemoteServiceSender.getInstance();

        List<SwiftServiceInfoBean> swiftServiceInfoBeans = serviceInfoService.getServiceInfoByService(SwiftClusterService.SERVICE);
        System.out.println(swiftServiceInfoBeans.isEmpty());
        SwiftServiceInfoBean swiftServiceInfoBean = swiftServiceInfoBeans.get(0);
        URL url = UrlSelector.getInstance().getFactory().getURL(swiftServiceInfoBean.getServiceInfo());
        SwiftServiceListenerHandler senderProxy = proxyFactory.getProxy(remoteServiceSender, SwiftServiceListenerHandler.class, url);

        SwiftHistoryService historyService = SwiftHistoryService.getInstance();
        historyService.setId(SwiftContext.getInstance().getBean("swiftProperty", SwiftProperty.class).getRpcAddress());
        LOGGER.info("begain to register " + historyService.getServiceType() + " to " + swiftServiceInfoBean.getClusterId() + "!");
        senderProxy.registerService(historyService);
        LOGGER.info("register " + historyService.getServiceType() + " to " + swiftServiceInfoBean.getClusterId() + " succeed!");
        SwiftIndexingService indexingService = SwiftIndexingService.getInstance();
        LOGGER.info("begain to register " + indexingService.getServiceType() + " to " + swiftServiceInfoBean.getClusterId() + "!");
        indexingService.setId(SwiftContext.getInstance().getBean("swiftProperty", SwiftProperty.class).getRpcAddress());
        senderProxy.registerService(indexingService);
        LOGGER.info("register " + indexingService.getServiceType() + " to " + swiftServiceInfoBean.getClusterId() + " succeed!");
    }

    //FR方式暂时不用
//    protected void masterLocalServiceRegister() {
//        //必须注册
//        FRProxyCache.registerInstance(RemoteServiceSender.class, RemoteServiceSender.getInstance());
//    }
//
//    protected void remoteServiceRegister() {
//        FRProxyCache.registerInstance(RemoteServiceSender.class, RemoteServiceSender.getInstance());
//
//        ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
//        String masterId = FRClusterNodeManager.getInstance().getMasterId();
//        try {
//            RemoteServiceSender senderProxy = (RemoteServiceSender) proxyFactory.getProxy((SwiftServiceListenerHandler) FRProxyCache.getInstance(RemoteServiceSender.class),
//                    SwiftServiceListenerHandler.class, new FRUrl(new FRDestination(masterId)));
//
//            String currentId = FRClusterNodeManager.getInstance().getCurrentId();
//
//            senderProxy.registerService(new SwiftRealtimeService(FRClusterNodeManager.getInstance().getCurrentId()));
//            senderProxy.registerService(new SwiftIndexingService(FRClusterNodeManager.getInstance().getCurrentId()));
//
//            SwiftHistoryService historyService = SwiftHistoryService.getInstance();
//            FRProxyCache.registerInstance(HistoryService.class, historyService);
//            historyService.setId(currentId);
//            senderProxy.registerService(historyService);
//
//            senderProxy.registerService(new SwiftAnalyseService(FRClusterNodeManager.getInstance().getCurrentId()));
//        } catch (ProxyRegisterException e) {
//        }
//    }
}
