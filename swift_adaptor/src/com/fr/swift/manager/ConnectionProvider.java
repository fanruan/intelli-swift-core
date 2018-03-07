package com.fr.swift.manager;

import com.finebi.conf.structure.bean.connection.FineConnection;
import com.finebi.conf.utils.FineConnectionUtils;
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
    @Override
    public ConnectionInfo getConnection(String connectionName) {
        try {
            FineConnection fineConnection = FineConnectionUtils.getConnectionByName(connectionName);
            return new SwiftConnectionInfo(fineConnection.getSchema(), fineConnection.getConnection());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
}
