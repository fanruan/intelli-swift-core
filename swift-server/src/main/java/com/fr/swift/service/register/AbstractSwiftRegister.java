package com.fr.swift.service.register;

import com.fr.swift.ProxyFactory;
import com.fr.swift.exception.ProxyRegisterException;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.frrpc.ClusterNodeManager;
import com.fr.swift.frrpc.FRDestination;
import com.fr.swift.frrpc.FRProxyCache;
import com.fr.swift.frrpc.FRUrl;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.service.HistoryService;
import com.fr.swift.service.SwiftAnalyseService;
import com.fr.swift.service.SwiftHistoryService;
import com.fr.swift.service.SwiftIndexingService;
import com.fr.swift.service.SwiftRealtimeService;
import com.fr.swift.service.SwiftRegister;
import com.fr.swift.service.listener.RemoteServiceSender;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;

/**
 * This class created on 2018/6/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class AbstractSwiftRegister implements SwiftRegister {

    protected void localServiceRegister() throws SwiftServiceException {
        new SwiftAnalyseService().start();
        SwiftHistoryService.getInstance().start();
        new SwiftIndexingService().start();
        new SwiftRealtimeService().start();
    }

    protected void masterLocalServiceRegister() {
        //必须注册
        FRProxyCache.registerInstance(RemoteServiceSender.class, RemoteServiceSender.getInstance());
    }

    protected void remoteServiceRegister() {
        FRProxyCache.registerInstance(RemoteServiceSender.class, RemoteServiceSender.getInstance());

        ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
        String masterId = ClusterNodeManager.getInstance().getMasterId();
        try {
            RemoteServiceSender senderProxy = (RemoteServiceSender) proxyFactory.getProxy((SwiftServiceListenerHandler) FRProxyCache.getInstance(RemoteServiceSender.class),
                    SwiftServiceListenerHandler.class, new FRUrl(new FRDestination(masterId)));

            String currentId = ClusterNodeManager.getInstance().getCurrentId();

            senderProxy.registerService(new SwiftRealtimeService(ClusterNodeManager.getInstance().getCurrentId()));
            senderProxy.registerService(new SwiftIndexingService(ClusterNodeManager.getInstance().getCurrentId()));

            SwiftHistoryService historyService = SwiftHistoryService.getInstance();
            FRProxyCache.registerInstance(HistoryService.class, historyService);
            historyService.setId(currentId);
            senderProxy.registerService(historyService);

            senderProxy.registerService(new SwiftAnalyseService(ClusterNodeManager.getInstance().getCurrentId()));
        } catch (ProxyRegisterException e) {
        }
    }
}
