package com.fr.swift.manager;

import com.finebi.base.stable.StableManager;
import com.finebi.conf.service.connection.FineConnectionService;
import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.db.ConnectionInfo;
import com.fr.swift.source.db.IConnectionProvider;
import com.fr.swift.source.db.SwiftConnectionInfo;

/**
 * Created by pony on 2018/2/27.
 */
public class ConnectionProvider implements IConnectionProvider {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ProviderManager.class);
    private FineConnectionService connectionService;

    @Override
    public ConnectionInfo getConnection(String connectionName) {
        try {
            JDBCDatabaseConnection connection = (JDBCDatabaseConnection) getConnectionService().getConnectionByName(connectionName);
            return new SwiftConnectionInfo(connection.getSchema(), connection);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    public FineConnectionService getConnectionService() {
        //这个不能static，再启动模块的时候会调用，然而引擎比功能先启动
        if (connectionService == null) {
            connectionService = StableManager.getContext().getObject("fineConnectionService");
        }
        return connectionService;
    }
}
