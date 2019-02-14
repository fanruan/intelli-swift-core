package com.fr.swift.boot;

import com.fineio.FineIO;
import com.fr.swift.SwiftContext;
import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.ProcessHandlerRegistry;
import com.fr.swift.basics.ServiceRegistry;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.base.ProxyProcessHandlerRegistry;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.cluster.listener.NodeStartedListener;
import com.fr.swift.config.PublicConfig;
import com.fr.swift.cube.queue.ProviderTaskManager;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.event.ClusterType;
import com.fr.swift.log.FineIoLogger;
import com.fr.swift.log.SwiftLog4jLoggers;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.segment.event.MaskHistoryListener;
import com.fr.swift.segment.event.PushSegmentLocationListener;
import com.fr.swift.segment.event.RemoveHistoryListener;
import com.fr.swift.segment.event.RemoveSegmentLocationListener;
import com.fr.swift.segment.event.TransferRealtimeListener;
import com.fr.swift.segment.event.UploadHistoryListener;
import com.fr.swift.service.local.LocalManager;
import com.fr.swift.service.local.ServerManager;

import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/6/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftEngineStart {

    public static void start(String[] args) {
        try {
            SwiftLoggers.setLoggerFactory(new SwiftLog4jLoggers());
            ClusterListenerHandler.addInitialListener(new SwiftClusterListener());
            SwiftContext.get().init();

            ClusterListenerHandler.addInitialListener(NodeStartedListener.INSTANCE);
            FineIO.setLogger(new FineIoLogger());
            ProviderTaskManager.start();
            SwiftCommandParser.parseCommand(args);
            registerProxy();
            PublicConfig.load();

            SwiftContext.get().getBean(LocalManager.class).startUp();
            if (SwiftProperty.getProperty().isCluster()) {
                ClusterListenerHandler.handlerEvent(new ClusterEvent(ClusterEventType.JOIN_CLUSTER, ClusterType.CONFIGURE));
            }
            SwiftContext.get().getBean(ServerManager.class).startUp();

            TransferRealtimeListener.listen();
            UploadHistoryListener.listen();
            MaskHistoryListener.listen();
            RemoveHistoryListener.listen();
            PushSegmentLocationListener.listen();
            RemoveSegmentLocationListener.listen();
            SwiftLoggers.getLogger().info("Swift engine start successful");
        } catch (Throwable e) {
            SwiftLoggers.getLogger().error(e);
            System.exit(1);
        }
    }

    private static void registerProxy() {
        //RPC远端可接收调用的SERVICE
        ServiceRegistry serviceRegistry = ProxyServiceRegistry.get();
        Map<String, Object> proxyServices = SwiftContext.get().getBeansByAnnotations(ProxyService.class);
        for (Map.Entry<String, Object> proxyService : proxyServices.entrySet()) {
            serviceRegistry.registerService(proxyService.getValue());
        }

        ProcessHandlerRegistry processHandlerRegistry = ProxyProcessHandlerRegistry.get();
        List<Class<?>> clazzList = SwiftContext.get().getClassesByAnnotations(RegisteredHandler.class);
        for (Class<?> clazz : clazzList) {
            try {
                RegisteredHandler registeredHandler = clazz.getAnnotation(RegisteredHandler.class);
                if (registeredHandler != null) {
                    processHandlerRegistry.addHandler(registeredHandler.value(), (Class<? extends ProcessHandler>) clazz);
                }
            } catch (IllegalArgumentException error) {
                SwiftLoggers.getLogger().error(error);
            }
        }
    }
}