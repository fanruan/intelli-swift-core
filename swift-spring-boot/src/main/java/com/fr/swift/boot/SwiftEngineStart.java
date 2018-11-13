package com.fr.swift.boot;

import com.fineio.FineIO;
import com.fr.config.dao.DaoContext;
import com.fr.config.dao.impl.LocalClassHelperDao;
import com.fr.config.dao.impl.LocalEntityDao;
import com.fr.config.dao.impl.LocalXmlEntityDao;
import com.fr.data.impl.Connection;
import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.swift.api.rpc.impl.DataMaintenanceServiceImpl;
import com.fr.swift.api.rpc.impl.DetectServiceImpl;
import com.fr.swift.api.rpc.impl.SelectServiceImpl;
import com.fr.swift.api.rpc.impl.TableServiceImpl;
import com.fr.swift.basics.base.ProxyProcessHandlerRegistry;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.basics.base.handler.SwiftMasterProcessHandler;
import com.fr.swift.basics.handler.CommonLoadProcessHandler;
import com.fr.swift.basics.handler.MasterProcessHandler;
import com.fr.swift.basics.handler.SyncDataProcessHandler;
import com.fr.swift.cluster.listener.NodeStartedListener;
import com.fr.swift.context.SwiftContext;
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
import com.fr.swift.service.SwiftHistoryService;
import com.fr.swift.service.SwiftIndexingService;
import com.fr.swift.service.SwiftRealtimeService;
import com.fr.swift.service.SwiftSyncDataProcessHandler;
import com.fr.swift.service.TransferRealtimeListener;
import com.fr.swift.service.UploadHistoryListener;
import com.fr.swift.service.listener.RemoteServiceSender;
import com.fr.swift.service.local.ServerManager;
import com.fr.swift.service.local.ServiceManager;
import com.fr.swift.source.db.ConnectionInfo;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.IConnectionProvider;
import com.fr.swift.source.db.SwiftConnectionInfo;
import com.fr.workspace.simple.SimpleWork;

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
            SimpleWork.checkIn(System.getProperty("user.dir"));
            ClusterListenerHandler.addInitialListener(new SwiftClusterListener());
            SwiftContext.init();

            registerTmpConnectionProvider();
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
        SwiftProperty property = SwiftProperty.getProperty();

        DaoContext.setEntityDao(new LocalEntityDao());
        DaoContext.setClassHelperDao(new LocalClassHelperDao());
        DaoContext.setXmlEntityDao(new LocalXmlEntityDao());

        Connection frConnection = new JDBCDatabaseConnection(property.getConfigDbDriverClass(),
                property.getConfigDbJdbcUrl(), property.getConfigDbUsername(), property.getConfigDbPasswd());
        final SwiftConnectionInfo connectionInfo = new SwiftConnectionInfo(null, frConnection);
        ConnectionManager.getInstance().registerProvider(new IConnectionProvider() {
            @Override
            public ConnectionInfo getConnection(String connectionName) {
                return connectionInfo;
            }
        });
    }

    private static void registerProxy() {
        //rpc远端可接收调用的service
        ProxyServiceRegistry.INSTANCE.registerService(new SwiftHistoryService());
        ProxyServiceRegistry.INSTANCE.registerService(new SwiftIndexingService());
        ProxyServiceRegistry.INSTANCE.registerService(new SwiftRealtimeService());
        ProxyServiceRegistry.INSTANCE.registerService(new SwiftAnalyseService());
        ProxyServiceRegistry.INSTANCE.registerService(new RemoteServiceSender());
        ProxyServiceRegistry.INSTANCE.registerService(new TableServiceImpl());
        ProxyServiceRegistry.INSTANCE.registerService(new DetectServiceImpl());
        ProxyServiceRegistry.INSTANCE.registerService(new DataMaintenanceServiceImpl());
        ProxyServiceRegistry.INSTANCE.registerService(new SelectServiceImpl());
        ProxyServiceRegistry.INSTANCE.registerService(new SwiftMasterService());
        ProxyServiceRegistry.INSTANCE.registerService(new SwiftSlaveService());

        //注解接口绑定的实现类
        ProxyProcessHandlerRegistry.INSTANCE.addHandler(MasterProcessHandler.class, SwiftMasterProcessHandler.class);
        ProxyProcessHandlerRegistry.INSTANCE.addHandler(NodesProcessHandler.class, SwiftNodesProcessHandler.class);
        ProxyProcessHandlerRegistry.INSTANCE.addHandler(CommonLoadProcessHandler.class, SwiftCommonLoadProcessHandler.class);
        ProxyProcessHandlerRegistry.INSTANCE.addHandler(SyncDataProcessHandler.class, SwiftSyncDataProcessHandler.class);
    }
}