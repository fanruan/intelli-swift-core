package com.fr.swift.boot;

import com.fineio.FineIO;
import com.fr.config.dao.DaoContext;
import com.fr.config.dao.impl.LocalClassHelperDao;
import com.fr.config.dao.impl.LocalEntityDao;
import com.fr.config.dao.impl.LocalXmlEntityDao;
import com.fr.data.impl.Connection;
import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.swift.cluster.listener.NodeStartedListener;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.queue.ProviderTaskManager;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.event.ClusterType;
import com.fr.swift.log.FineIOLoggerImpl;
import com.fr.swift.log.SwiftLog4jLoggers;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.MaskHistoryListener;
import com.fr.swift.service.RemoveHistoryListener;
import com.fr.swift.service.TransferRealtimeListener;
import com.fr.swift.service.UploadHistoryListener;
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
            ClusterListenerHandler.addListener(new SwiftClusterListener());
            SwiftContext.init();

            registerTmpConnectionProvider();
            ClusterListenerHandler.addListener(NodeStartedListener.INSTANCE);
            FineIO.setLogger(new FineIOLoggerImpl());
            ProviderTaskManager.start();
            SwiftCommandParser.parseCommand(args);

            SwiftContext.get().getBean("localManager", ServiceManager.class).startUp();
            if (SwiftContext.get().getBean("swiftProperty", SwiftProperty.class).isCluster()) {
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
        SwiftProperty property = SwiftContext.get().getBean(SwiftProperty.class);

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
}