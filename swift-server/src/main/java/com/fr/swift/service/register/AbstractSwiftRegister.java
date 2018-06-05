package com.fr.swift.service.register;

import com.fr.cluster.engine.ticket.FineClusterToolKit;
import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.exception.ProxyRegisterException;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.frrpc.ClusterNodeManager;
import com.fr.swift.frrpc.FRDestination;
import com.fr.swift.frrpc.FRProxyCache;
import com.fr.swift.frrpc.FRUrl;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.service.SwiftAnalyseService;
import com.fr.swift.service.SwiftHistoryService;
import com.fr.swift.service.SwiftIndexingService;
import com.fr.swift.service.SwiftRealTimeService;
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
        new SwiftHistoryService().start();
        new SwiftIndexingService().start();
        new SwiftRealTimeService().start();
    }

    protected void masterLocalServiceRegister() {
        //必须注册
        FineClusterToolKit.getInstance().getRPCProxyFactory().newBuilder(RemoteServiceSender.getInstance()).build();
        FRProxyCache.registerInstance(RemoteServiceSender.class, RemoteServiceSender.getInstance());
    }

    protected void remoteServiceRegister() {
        FineClusterToolKit.getInstance().getRPCProxyFactory().newBuilder(RemoteServiceSender.getInstance()).build();
        FRProxyCache.registerInstance(RemoteServiceSender.class, RemoteServiceSender.getInstance());

        ProxyFactory proxyFactory = ProxySelector.getInstance().getFactory();
        String masterId = ClusterNodeManager.getInstance().getMasterId();
        try {
            Invoker invoker = proxyFactory.getInvoker((SwiftServiceListenerHandler) FRProxyCache.getInstance(RemoteServiceSender.class), SwiftServiceListenerHandler.class
                    , new FRUrl(new FRDestination(masterId)));
            SwiftServiceListenerHandler senderProxy = (SwiftServiceListenerHandler) proxyFactory.getProxy(invoker);
            senderProxy.registerService(new SwiftRealTimeService(ClusterNodeManager.getInstance().getCurrentId()));
            senderProxy.registerService(new SwiftIndexingService(ClusterNodeManager.getInstance().getCurrentId()));
            senderProxy.registerService(new SwiftHistoryService(ClusterNodeManager.getInstance().getCurrentId()));
            senderProxy.registerService(new SwiftAnalyseService(ClusterNodeManager.getInstance().getCurrentId()));
        } catch (ProxyRegisterException e) {
        } catch (Exception e) {
        }
    }
}
