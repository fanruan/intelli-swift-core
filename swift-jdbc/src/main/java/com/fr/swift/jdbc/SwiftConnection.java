package com.fr.swift.jdbc;

import com.fr.swift.api.rpc.exception.ConnectException;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.jdbc.exception.URLEmptyException;
import com.fr.swift.jdbc.exception.URLFormatException;
import com.fr.swift.jdbc.mode.Mode;
import com.fr.swift.jdbc.session.SwiftJdbcSessionFactory;
import com.fr.swift.jdbc.session.impl.SwiftJdbcSessionFactoryImpl;

import java.net.URI;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author pony
 * @date 2018/8/17
 */
public class SwiftConnection extends BaseSwiftConnection {
    private static final String JDBC_HEAD = "jdbc:swift";
    private static final String EMPTY = "";
    private static final String SEPARATOR = "/";
    private SwiftDatabase database;
    private String host;
    private int port;
    private String username;
    private String password;
    private SwiftJdbcSessionFactory sessionFactory;
    private Properties properties;
    private boolean close;

    SwiftConnection(String url, Properties properties) throws SQLException {
        if (null != url) {
            if (url.toLowerCase().startsWith(JDBC_HEAD)) {
                this.properties = properties;
                url = url.substring(JDBC_HEAD.length() + 1);
                URI uri = URI.create(url);
                String schema = uri.getScheme();
                Mode mode;
                try {
                    mode = Mode.fromKey(schema);
                } catch (Exception e) {
                    throw new URLFormatException(url);
                }
                String dbName = uri.getPath();
                setSchema(dbName);
                if (mode == Mode.SERVER) {
                    connectServerMode(uri);
                } else {
                    sessionFactory = new SwiftJdbcSessionFactoryImpl(database);
                }
            } else {
                throw new URLFormatException(url);
            }
        } else {
            throw new URLEmptyException();
        }
    }

    private void connectServerMode(URI uri) {
        host = uri.getHost();
        port = uri.getPort();
        port = port == -1 ? 7000 : port;
        if (null != properties) {
            this.username = properties.getProperty("user", "swift");
            this.password = properties.getProperty("password", EMPTY);
        }
        try {
            sessionFactory = new SwiftJdbcSessionFactoryImpl(database, host, port);
        } catch (ConnectException e) {
            close();
            throw e;
        }
    }

    @Override
    public Statement createStatement() throws SQLException {
        return sessionFactory.openSession().createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return sessionFactory.openSession().preparedStatement(sql);
    }

    @Override
    public void close() {
        close = true;
        if (null != sessionFactory) {
            sessionFactory.close();
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return close;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return sessionFactory.openSession().getDatabaseMetaData();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return sessionFactory.openSession().createStatement();
    }

    @Override
    public int getHoldability() throws SQLException {
        //先随便return一个，暂时没什么用
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    public String getSchema() throws SQLException {
        return database.getName();
    }

    public void setSchema(String dbName) {
        if (null == dbName || EMPTY.equals(dbName.trim())) {
            database = SwiftDatabase.DECISION_LOG;
        }
        dbName = dbName.startsWith(SEPARATOR) ? dbName.substring(1) : dbName;
        try {
            database = SwiftDatabase.valueOf(dbName.toUpperCase());
        } catch (Exception e) {
            database = SwiftDatabase.DECISION_LOG;
        }
    }


}
