package com.fr.swift.jdbc.sql;

import com.fr.swift.jdbc.BuildInConnectionProperty;
import com.fr.swift.jdbc.JdbcProperty;
import com.fr.swift.jdbc.Mode;
import com.fr.swift.jdbc.SwiftJdbcConstants;
import com.fr.swift.jdbc.exception.Exceptions;
import com.fr.swift.jdbc.request.RequestService;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
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
    protected Holder holder;

    public UnregisteredDriver() {
        this.holder = new Holder();
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (null == url || SwiftJdbcConstants.EMPTY.equals(url.trim())) {
            throw Exceptions.urlEmpty();
        }
        String testUrl = url.trim().toLowerCase();
        if (!acceptsURL(testUrl)) {
            return null;
        }
        holder.connectUri = URI.create(testUrl.substring(getConnectionSchema().length() + 1));
        String schema = holder.connectUri.getScheme();
        if (null == schema) {
            throw Exceptions.urlFormat(url);
        }
        Mode mode;
        try {
            mode = Mode.fromKey(schema);
        } catch (Exception e) {
            throw Exceptions.urlFormat(url);
        }
        if (Mode.EMB.equals(mode)) {
            try {
                Class clazz = Class.forName("com.fr.swift.jdbc.sql.EmbSwiftConnection");
                Constructor constructor = clazz.getDeclaredConstructor(UnregisteredDriver.class, Properties.class);
                constructor.setAccessible(true);
                return (Connection) constructor.newInstance(this, info);
            } catch (Exception e) {
                throw Exceptions.environment(e);
            }
        } else {
            return new RemoteConnection(this, info);
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
        for (BuildInConnectionProperty value : BuildInConnectionProperty.values()) {
            list.add(new DriverPropertyInfo(value.getPropertyName(), value.getDefaultValue().toString()));
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
        return Logger.getLogger(SwiftJdbcConstants.EMPTY);
    }

    protected void register() {
        try {
            DriverManager.registerDriver(this);
        } catch (SQLException ignore) {
        }
    }

    /**
     * TODO request Service没有初始化
     */
    public static class Holder {
        private URI connectUri;
        private RequestService requestService;
        private String authCode;

        public URI getConnectUri() {
            return connectUri;
        }

        public String getAuthCode() {
            return authCode;
        }

        public RequestService getRequestService() {
            return requestService;
        }
    }
}
