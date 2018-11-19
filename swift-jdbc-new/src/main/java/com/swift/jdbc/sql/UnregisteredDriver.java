package com.swift.jdbc.sql;

import com.swift.jdbc.JdbcProperty;
import com.swift.jdbc.Mode;
import com.swift.jdbc.exception.Exceptions;
import com.swift.jdbc.request.RequestService;

import java.net.URI;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author yee
 * @date 2018/11/16
 */
public abstract class UnregisteredDriver implements Driver {
    private static final String DEFAULT_DATABASE = "DECISION_LOG";
    private static final String EMPTY = "";
    private static final String SEPARATOR = "/";
    protected Holder holder;

    public UnregisteredDriver() {
        this.holder = new Holder();
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (null == url || EMPTY.equals(url.trim())) {
            throw Exceptions.urlEmpty();
        }
        String testUrl = url.trim().toLowerCase();
        if (!acceptsURL(testUrl)) {
            return null;
        }
        URI uri = URI.create(testUrl.substring(getConnectionSchema().length() + 1));
        String schema = uri.getScheme();
        if (null == schema) {
            throw Exceptions.urlFormat(url);
        }
        try {
            Mode mode = Mode.fromKey(schema);
            String database = handleDatabase(uri.getPath());
            if (Mode.EMB.equals(mode)) {
                return new EmbSwiftConnection(this, database);
            } else {
                return new RemoteConnection(this, database);
            }
        } catch (Exception e) {
            throw Exceptions.urlFormat(url);
        }
    }

    @Override
    public boolean acceptsURL(String url) {
        return url.startsWith(getConnectionSchema());
    }

    /**
     * getConnectionSchema
     *
     * @return schema like jdbc:swift
     */
    protected abstract String getConnectionSchema();

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
        List<DriverPropertyInfo> list = new ArrayList<DriverPropertyInfo>();
        for (Map.Entry<Object, Object> entry : info.entrySet()) {
            list.add(new DriverPropertyInfo((String) entry.getKey(), (String) entry.getValue()));
        }
        return list.toArray(new DriverPropertyInfo[list.size()]);
    }

    @Override
    public int getMajorVersion() {
        return JdbcProperty.get().getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        return JdbcProperty.get().getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant() {
        return JdbcProperty.get().isCompliant();
    }

    public Logger getParentLogger() {
        return Logger.getLogger(EMPTY);
    }

    public String handleDatabase(String dbName) {
        if (null == dbName || EMPTY.equals(dbName.trim())) {
            return DEFAULT_DATABASE;
        }
        return dbName.startsWith(SEPARATOR) ? dbName.substring(1).toUpperCase() : dbName.toUpperCase();
    }

    public static class Holder {
        private RequestService requestService;

        public RequestService getRequestService() {
            return requestService;
        }
    }
}
