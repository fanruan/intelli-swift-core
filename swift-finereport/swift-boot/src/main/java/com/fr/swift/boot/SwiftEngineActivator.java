package com.fr.swift.boot;

import com.fineio.FineIO;
import com.fr.cluster.entry.ClusterTicketKey;
import com.fr.io.base.WebInfResourceFolders;
import com.fr.log.impl.MetricInvocationHandler;
import com.fr.module.Activator;
import com.fr.module.extension.Prepare;
import com.fr.stable.db.constant.BaseDBConstant;
import com.fr.swift.SwiftContext;
import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.api.rpc.DetectService;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.TableService;
import com.fr.swift.basics.ProcessHandlerRegistry;
import com.fr.swift.basics.ServiceRegistry;
import com.fr.swift.basics.base.ProxyProcessHandlerRegistry;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.basics.base.handler.SwiftAppointProcessHandler;
import com.fr.swift.basics.base.handler.SwiftMasterProcessHandler;
import com.fr.swift.basics.handler.AliveNodesProcessHandler;
import com.fr.swift.basics.handler.AppointProcessHandler;
import com.fr.swift.basics.handler.CollateProcessHandler;
import com.fr.swift.basics.handler.CommonLoadProcessHandler;
import com.fr.swift.basics.handler.CommonProcessHandler;
import com.fr.swift.basics.handler.InsertSegmentProcessHandler;
import com.fr.swift.basics.handler.MasterProcessHandler;
import com.fr.swift.basics.handler.NodesProcessHandler;
import com.fr.swift.basics.handler.QueryableProcessHandler;
import com.fr.swift.basics.handler.SyncDataProcessHandler;
import com.fr.swift.boot.upgrade.UpgradeTask;
import com.fr.swift.cluster.listener.NodeStartedListener;
import com.fr.swift.cluster.service.MasterService;
import com.fr.swift.cluster.service.SlaveService;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.context.SwiftConfigContext;
import com.fr.swift.cube.queue.ProviderTaskManager;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.log.SwiftFrLoggers;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.process.handler.SwiftAliveNodesProcessHandler;
import com.fr.swift.process.handler.SwiftCommonProcessHandler;
import com.fr.swift.process.handler.SwiftNodesProcessHandler;
import com.fr.swift.segment.container.SegmentContainer;
import com.fr.swift.segment.event.MaskHistoryListener;
import com.fr.swift.segment.event.PushSegmentLocationListener;
import com.fr.swift.segment.event.RemoveHistoryListener;
import com.fr.swift.segment.event.RemoveSegmentLocationListener;
import com.fr.swift.segment.event.TransferRealtimeListener;
import com.fr.swift.segment.event.UploadHistoryListener;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.service.DeleteService;
import com.fr.swift.service.HistoryService;
import com.fr.swift.service.IndexingService;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.service.SwiftCollateProcessHandler;
import com.fr.swift.service.SwiftCommonLoadProcessHandler;
import com.fr.swift.service.SwiftInsertSegmentProcessHandler;
import com.fr.swift.service.SwiftQueryableProcessHandler;
import com.fr.swift.service.SwiftSyncDataProcessHandler;
import com.fr.swift.service.UploadService;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.service.local.LocalManager;
import com.fr.swift.service.local.ServiceManager;
import com.fr.swift.util.concurrent.CommonExecutor;

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

        FineIO.setLogger(SwiftLoggers.getLogger());

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
        //rpc远端可接收调用的service
        ServiceRegistry serviceRegistry = ProxyServiceRegistry.get();
        serviceRegistry.registerService(SwiftContext.get().getBean(HistoryService.class));
        serviceRegistry.registerService(SwiftContext.get().getBean(IndexingService.class));
        serviceRegistry.registerService(SwiftContext.get().getBean(RealtimeService.class));
        serviceRegistry.registerService(SwiftContext.get().getBean(AnalyseService.class));
        serviceRegistry.registerService(SwiftContext.get().getBean(RemoteSender.class));
        serviceRegistry.registerService(SwiftContext.get().getBean(TableService.class));
        serviceRegistry.registerService(SwiftContext.get().getBean(DetectService.class));
        serviceRegistry.registerService(SwiftContext.get().getBean(DataMaintenanceService.class));
        serviceRegistry.registerService(SwiftContext.get().getBean(SelectService.class));
        serviceRegistry.registerService(SwiftContext.get().getBean(MasterService.class));
        serviceRegistry.registerService(SwiftContext.get().getBean(SlaveService.class));

        serviceRegistry.registerService(SwiftContext.get().getBean(DeleteService.class));
        serviceRegistry.registerService(SwiftContext.get().getBean(UploadService.class));

        //注解接口绑定的实现类
        ProcessHandlerRegistry processHandlerRegistry = ProxyProcessHandlerRegistry.get();
        processHandlerRegistry.addHandler(MasterProcessHandler.class, SwiftMasterProcessHandler.class);
        processHandlerRegistry.addHandler(NodesProcessHandler.class, SwiftNodesProcessHandler.class);
        processHandlerRegistry.addHandler(CommonLoadProcessHandler.class, SwiftCommonLoadProcessHandler.class);
        processHandlerRegistry.addHandler(SyncDataProcessHandler.class, SwiftSyncDataProcessHandler.class);
        processHandlerRegistry.addHandler(InsertSegmentProcessHandler.class, SwiftInsertSegmentProcessHandler.class);
        processHandlerRegistry.addHandler(QueryableProcessHandler.class, SwiftQueryableProcessHandler.class);
        processHandlerRegistry.addHandler(AliveNodesProcessHandler.class, SwiftAliveNodesProcessHandler.class);
        processHandlerRegistry.addHandler(CommonProcessHandler.class, SwiftCommonProcessHandler.class);
        processHandlerRegistry.addHandler(AppointProcessHandler.class, SwiftAppointProcessHandler.class);
        processHandlerRegistry.addHandler(CollateProcessHandler.class, SwiftCollateProcessHandler.class);
    }
}