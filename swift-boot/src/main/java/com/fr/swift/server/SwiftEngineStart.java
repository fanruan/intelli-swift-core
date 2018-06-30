package com.fr.swift.server;

import com.fr.config.BaseDBEnv;
import com.fr.config.dao.DaoContext;
import com.fr.config.dao.impl.HibernateClassHelperDao;
import com.fr.config.dao.impl.HibernateEntityDao;
import com.fr.config.dao.impl.HibernateXmlEnityDao;
import com.fr.config.entity.ClassHelper;
import com.fr.config.entity.Entity;
import com.fr.config.entity.XmlEntity;
import com.fr.data.impl.Connection;
import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.file.ConnectionConfig;
import com.fr.general.ComparatorUtils;
import com.fr.stable.db.DBContext;
import com.fr.stable.db.option.DBOption;
import com.fr.swift.boot.ClusterListener;
import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.entity.SwiftServiceInfoEntity;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.queue.ProviderTaskManager;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.event.ClusterType;
import com.fr.swift.http.SwiftHttpServer;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.register.LocalSwiftRegister;
import com.fr.swift.source.db.ConnectionInfo;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.IConnectionProvider;
import com.fr.swift.source.db.SwiftConnectionInfo;
import com.fr.transaction.Configurations;
import com.fr.transaction.FineConfigurationHelper;
import com.fr.workspace.simple.SimpleWork;

/**
 * This class created on 2018/6/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftEngineStart {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftEngineStart.class);

    public static void main(String[] args) {
        try {
            SimpleWork.checkIn(System.getProperty("user.dir"));
            SwiftContext.init();
            SwiftContext.getInstance().getBean(SwiftHttpServer.class).start();
            LOGGER.info("http server starting!");
            initConfDB();
            registerTmpConnectionProvider();
            new LocalSwiftRegister().serviceRegister();
            ClusterListenerHandler.addListener(new ClusterListener());
            ProviderTaskManager.start();
            if (SwiftContext.getInstance().getBean("swiftProperty", SwiftProperty.class).isCluster()) {
                ClusterListenerHandler.handlerEvent(new ClusterEvent(ClusterEventType.JOIN_CLUSTER, ClusterType.CONFIGURE));
            }
        } catch (Throwable e) {
            LOGGER.error(e);
            System.exit(1);
        }
    }

    private static void initConfDB() throws Exception {
        SwiftProperty property = SwiftContext.getInstance().getBean("swiftProperty", SwiftProperty.class);
        DBOption dbOption = new DBOption();
        dbOption.setUrl(property.getConfigDbJdbcUrl());
        dbOption.setUsername(property.getConfigDbUsername());
        dbOption.setPassword(property.getConfigDbPasswd());
        dbOption.setDriverClass(property.getConfigDbDriverClass());
        dbOption.setDialectClass("com.fr.third.org.hibernate.dialect.MySQL5Dialect");
        dbOption.addRawProperty("hibernate.show_sql", false)
                .addRawProperty("hibernate.format_sql", true).addRawProperty("hibernate.connection.autocommit", false);
        DBContext dbProvider = DBContext.create();
        dbProvider.addEntityClass(Entity.class);
        dbProvider.addEntityClass(XmlEntity.class);
        dbProvider.addEntityClass(ClassHelper.class);

        dbProvider.addEntityClass(SwiftMetaDataEntity.class);
        dbProvider.addEntityClass(SwiftSegmentEntity.class);
        dbProvider.addEntityClass(SwiftServiceInfoEntity.class);

        dbProvider.init(dbOption);
        BaseDBEnv.setDBContext(dbProvider);
        DaoContext.setClassHelperDao(new HibernateClassHelperDao());
        DaoContext.setXmlEntityDao(new HibernateXmlEnityDao());
        DaoContext.setEntityDao(new HibernateEntityDao());
        Configurations.setHelper(new FineConfigurationHelper());
    }

    private static void registerTmpConnectionProvider() {
        SwiftProperty property = SwiftContext.getInstance().getBean(SwiftProperty.class);
        Connection frConnection = new JDBCDatabaseConnection(property.getConfigDbDriverClass(),
                property.getConfigDbJdbcUrl(), property.getConfigDbUsername(), property.getConfigDbPasswd());
        final SwiftConnectionInfo connectionInfo = new SwiftConnectionInfo(null, frConnection);
        ConnectionManager.getInstance().registerProvider(new IConnectionProvider() {
            @Override
            public ConnectionInfo getConnection(String connectionName) {
                if (ComparatorUtils.equals(connectionName, "test")) {
                    return connectionInfo;
                }
                Connection connection = ConnectionConfig.getInstance().getConnection(connectionName);
                if (null != connection) {
                    return new SwiftConnectionInfo(connectionName, connection);
                }
                return connectionInfo;
            }
        });
    }
}
