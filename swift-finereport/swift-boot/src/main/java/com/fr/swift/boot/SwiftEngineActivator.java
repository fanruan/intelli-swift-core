package com.fr.swift.boot;

import com.fineio.FineIO;
import com.fr.cluster.entry.ClusterTicketKey;
import com.fr.io.base.WebInfResourceFolders;
import com.fr.log.impl.MetricInvocationHandler;
import com.fr.module.Activator;
import com.fr.module.extension.Prepare;
import com.fr.stable.db.constant.BaseDBConstant;
import com.fr.swift.SwiftContext;
import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.ProcessHandlerRegistry;
import com.fr.swift.basics.ServiceRegistry;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.annotation.RegisteredHandler;
import com.fr.swift.basics.base.ProxyProcessHandlerRegistry;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.boot.upgrade.UpgradeTask;
import com.fr.swift.cluster.listener.NodeStartedListener;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.context.SwiftConfigContext;
import com.fr.swift.cube.queue.ProviderTaskManager;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.log.FineIoLogger;
import com.fr.swift.log.SwiftFrLoggers;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.container.SegmentContainer;
import com.fr.swift.segment.event.MaskHistoryListener;
import com.fr.swift.segment.event.PushSegmentLocationListener;
import com.fr.swift.segment.event.RemoveHistoryListener;
import com.fr.swift.segment.event.RemoveSegmentLocationListener;
import com.fr.swift.segment.event.TransferRealtimeListener;
import com.fr.swift.segment.event.UploadHistoryListener;
import com.fr.swift.service.local.LocalManager;
import com.fr.swift.service.local.ServiceManager;
import com.fr.swift.util.concurrent.CommonExecutor;

import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/5/24
 */
public class SwiftEngineActivator extends Activator implements Prepare {

    static {
        SwiftLoggers.setLoggerFactory(new SwiftFrLoggers());
    }

    @Override
    public void start() {
        try {
            startSwift();
            SwiftLoggers.getLogger().info("swift engine started");
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("swift engine start failed", e);
        }
    }

    private void startSwift() {
        SwiftContext.get().init();
        upgrade();

        FineIO.setLogger(new FineIoLogger());

        ClusterListenerHandler.addInitialListener(new FRClusterListener());

        CommonExecutor.get().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MetricInvocationHandler.getInstance().doAfterSwiftContextInit();
                    ClusterListenerHandler.addInitialListener(NodeStartedListener.INSTANCE);
                    SwiftConfigContext.getInstance().init();
                    registerProxy();
                    SwiftContext.get().getBean(LocalManager.class).startUp();
                    ProviderTaskManager.start();
                    TransferRealtimeListener.listen();
                    UploadHistoryListener.listen();
                    MaskHistoryListener.listen();
                    RemoveHistoryListener.listen();

                    PushSegmentLocationListener.listen();
                    RemoveSegmentLocationListener.listen();
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error("swift engine start failed", e);
                }
            }
        });

        registerResourceIoPath();
    }

    private static void registerResourceIoPath() {
        for (SwiftDatabase schema : SwiftDatabase.values()) {
            WebInfResourceFolders.add(String.format("%s", schema.getDir()));
        }
    }

    private void upgrade() {
        new UpgradeTask().run();
    }

    @Override
    public void stop() {
        try {
            SwiftContext.get().getBean(ServiceManager.class).shutDown();
            for (SegmentContainer container : SegmentContainer.values()) {
                container.clear();
            }
            SwiftLoggers.getLogger().info("swift engine stopped");
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("swift engine stop failed", e);
        }
    }

    @Override
    public void prepare() {
        this.addMutable(ClusterTicketKey.KEY, SwiftClusterTicket.getInstance());
        this.addMutable(BaseDBConstant.BASE_ENTITY_KEY, SwiftConfigConstants.ENTITIES);
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