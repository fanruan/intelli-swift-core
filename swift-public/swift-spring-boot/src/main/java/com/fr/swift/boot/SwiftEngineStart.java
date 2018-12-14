package com.fr.swift.boot;

import com.fineio.FineIO;
import com.fr.swift.SwiftContext;
import com.fr.swift.api.rpc.impl.DataMaintenanceServiceImpl;
import com.fr.swift.api.rpc.impl.DetectServiceImpl;
import com.fr.swift.api.rpc.impl.SelectServiceImpl;
import com.fr.swift.api.rpc.impl.TableServiceImpl;
import com.fr.swift.basics.ProcessHandlerRegistry;
import com.fr.swift.basics.ServiceRegistry;
import com.fr.swift.basics.base.ProxyProcessHandlerRegistry;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.basics.base.handler.SwiftMasterProcessHandler;
import com.fr.swift.service.SwiftQueryableProcessHandler;
import com.fr.swift.basics.handler.CommonLoadProcessHandler;
import com.fr.swift.basics.handler.DeleteSegmentProcessHandler;
import com.fr.swift.basics.handler.InsertSegmentProcessHandler;
import com.fr.swift.basics.handler.MasterProcessHandler;
import com.fr.swift.basics.handler.QueryableProcessHandler;
import com.fr.swift.basics.handler.SyncDataProcessHandler;
import com.fr.swift.cluster.listener.NodeStartedListener;
import com.fr.swift.cube.queue.ProviderTaskManager;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.event.ClusterType;
import com.fr.swift.log.SwiftLog4jLoggers;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.nm.service.SwiftSlaveService;
import com.fr.swift.process.handler.NodesProcessHandler;
import com.fr.swift.process.handler.SwiftNodesProcessHandler;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.rm.service.SwiftMasterService;
import com.fr.swift.service.MaskHistoryListener;
import com.fr.swift.service.RemoveHistoryListener;
import com.fr.swift.service.SwiftAnalyseService;
import com.fr.swift.service.SwiftCommonLoadProcessHandler;
import com.fr.swift.service.SwiftDeleteSegmentProcessHandler;
import com.fr.swift.service.SwiftHistoryService;
import com.fr.swift.service.SwiftIndexingService;
import com.fr.swift.service.SwiftInsertSegmentProcessHandler;
import com.fr.swift.service.SwiftRealtimeService;
import com.fr.swift.service.SwiftSyncDataProcessHandler;
import com.fr.swift.service.TransferRealtimeListener;
import com.fr.swift.service.UploadHistoryListener;
import com.fr.swift.service.listener.RemoteServiceSender;
import com.fr.swift.service.local.ServerManager;
import com.fr.swift.service.local.ServiceManager;

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
//            SimpleWork.checkIn(System.getProperty("user.dir"));
            ClusterListenerHandler.addInitialListener(new SwiftClusterListener());
            SwiftContext.get().init();

//            registerTmpConnectionProvider();
            ClusterListenerHandler.addInitialListener(NodeStartedListener.INSTANCE);
            FineIO.setLogger(SwiftLoggers.getLogger());
            ProviderTaskManager.start();
            SwiftCommandParser.parseCommand(args);
            registerProxy();
            SwiftContext.get().getBean("localManager", ServiceManager.class).startUp();
            if (SwiftProperty.getProperty().isCluster()) {
                ClusterListenerHandler.handlerEvent(new ClusterEvent(ClusterEventType.JOIN_CLUSTER, ClusterType.CONFIGURE));
            }
            SwiftContext.get().getBean(ServerManager.class).startUp();

            TransferRealtimeListener.listen();
            UploadHistoryListener.listen();
            MaskHistoryListener.listen();
            RemoveHistoryListener.listen();


            SwiftLoggers.getLogger().info("Swift engine start successful");
        } catch (Throwable e) {
            SwiftLoggers.getLogger().error(e);
            System.exit(1);
        }
    }

    private static void registerTmpConnectionProvider() {
//        SwiftProperty property = SwiftProperty.getProperty();
//
//        DaoContext.setEntityDao(new LocalEntityDao());
//        DaoContext.setClassHelperDao(new LocalClassHelperDao());
//        DaoContext.setXmlEntityDao(new LocalXmlEntityDao());
//
//        Connection frConnection = new JDBCDatabaseConnection(property.getConfigDbDriverClass(),
//                property.getConfigDbJdbcUrl(), property.getConfigDbUsername(), property.getConfigDbPasswd());
//        final SwiftConnectionInfo connectionInfo = new SwiftConnectionInfo(null, frConnection);
//        ConnectionManager.getInstance().registerProvider(new IConnectionProvider() {
//            @Override
//            public ConnectionInfo getConnection(String connectionName) {
//                return connectionInfo;
//            }
//        });
    }

    private static void registerProxy() {
        //rpc远端可接收调用的service
        ServiceRegistry serviceRegistry = ProxyServiceRegistry.get();
        serviceRegistry.registerService(new SwiftHistoryService());
        serviceRegistry.registerService(new SwiftIndexingService());
        serviceRegistry.registerService(new SwiftRealtimeService());
        serviceRegistry.registerService(new SwiftAnalyseService());
        serviceRegistry.registerService(new RemoteServiceSender());
        serviceRegistry.registerService(new TableServiceImpl());
        serviceRegistry.registerService(new DetectServiceImpl());
        serviceRegistry.registerService(new DataMaintenanceServiceImpl());
        serviceRegistry.registerService(new SelectServiceImpl());
        serviceRegistry.registerService(new SwiftMasterService());
        serviceRegistry.registerService(new SwiftSlaveService());

        //注解接口绑定的实现类
        ProcessHandlerRegistry processHandlerRegistry = ProxyProcessHandlerRegistry.get();
        processHandlerRegistry.addHandler(MasterProcessHandler.class, SwiftMasterProcessHandler.class);
        processHandlerRegistry.addHandler(NodesProcessHandler.class, SwiftNodesProcessHandler.class);
        processHandlerRegistry.addHandler(CommonLoadProcessHandler.class, SwiftCommonLoadProcessHandler.class);
        processHandlerRegistry.addHandler(SyncDataProcessHandler.class, SwiftSyncDataProcessHandler.class);
        processHandlerRegistry.addHandler(DeleteSegmentProcessHandler.class, SwiftDeleteSegmentProcessHandler.class);
        processHandlerRegistry.addHandler(InsertSegmentProcessHandler.class, SwiftInsertSegmentProcessHandler.class);
        processHandlerRegistry.addHandler(QueryableProcessHandler.class, SwiftQueryableProcessHandler.class);
    }
}