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
    private static FineConnectionService connService = StableManager.getContext().getObject("fineConnectionService");

    @Override
    public ConnectionInfo getConnection(String connectionName) {
        try {
            JDBCDatabaseConnection connection = (JDBCDatabaseConnection) connService.getConnectionByName(connectionName);
            return new SwiftConnectionInfo(connection.getSchema(),connection);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
}
