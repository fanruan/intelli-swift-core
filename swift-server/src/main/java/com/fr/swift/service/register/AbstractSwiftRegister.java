package com.fr.swift.service.register;

import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.SwiftRegister;
import com.fr.third.springframework.beans.factory.annotation.Autowired;

/**
 * This class created on 2018/6/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class AbstractSwiftRegister implements SwiftRegister {

    @Autowired
    protected SwiftProperty swiftProperty;

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
