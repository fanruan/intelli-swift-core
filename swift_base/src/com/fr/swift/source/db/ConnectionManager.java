package com.fr.swift.source.db;

import com.fr.swift.provider.ConnectionProvider;
import com.fr.swift.util.Util;

/**
 * Created by pony on 2017/11/22.
 */
public class ConnectionManager {
    private static final ConnectionManager instance = new ConnectionManager();

    public IConnectionProvider provider;

    private ConnectionManager() {
        this.provider = new ConnectionProvider();
    }

    public static ConnectionManager getInstance() {
        return instance;
    }

    public void registerProvider(IConnectionProvider provider) {
        this.provider = provider;
    }

    public ConnectionInfo getConnectionInfo(String connectionName) {
        Util.requireNonNull(provider);
        return provider.getConnection(connectionName);
    }

    public void registerConnectionInfo(String connectionName, ConnectionInfo connectionInfo) {
        if (provider == null) {
            this.registerProvider(new ConnectionProvider());
        }
        provider.register(connectionName, connectionInfo);
    }
}
