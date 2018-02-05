package com.fr.swift.provider;

import com.fr.swift.source.db.ConnectionInfo;
import com.fr.swift.source.db.IConnectionProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2017-12-19 15:29:36
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class ConnectionProvider implements IConnectionProvider {

    private Map<String, ConnectionInfo> connectionInfoMap;

    private Object lock = new Object();

    public ConnectionProvider(Map<String, ConnectionInfo> connectionInfoMap) {
        this.connectionInfoMap = connectionInfoMap;
    }

    public ConnectionProvider() {
        this.connectionInfoMap = new ConcurrentHashMap<String, ConnectionInfo>();
    }

    @Override
    public ConnectionInfo getConnection(String connectionName) {
        return connectionInfoMap.get(connectionName);
    }

    @Override
    public void register(String connectionName, ConnectionInfo connectionInfo) {
        connectionInfoMap.put(connectionName, connectionInfo);
    }

}
