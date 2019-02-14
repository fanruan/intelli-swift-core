package com.fr.swift.jdbc.sql;

import com.fr.swift.api.info.RequestInfo;
import com.fr.swift.api.server.response.ApiResponse;
import com.fr.swift.jdbc.BuildInConnectionProperty;
import com.fr.swift.jdbc.SwiftJdbcConstants;
import com.fr.swift.jdbc.checker.GrammarChecker;
import com.fr.swift.jdbc.exception.Exceptions;
import com.fr.swift.jdbc.rpc.JdbcExecutor;

import java.io.File;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @author yee
 * @date 2018/11/16
 */
public abstract class BaseSwiftConnection implements Connection {

    protected UnregisteredDriver driver;
    private String database;
    protected SwiftStatementHolder holder;
    private Properties properties;


    BaseSwiftConnection(UnregisteredDriver driver, Properties properties) {
        this.driver = driver;
        this.database = handleDatabase(driver.holder.getConnectUri().getPath());
        this.properties = properties;
        this.holder = new SwiftStatementHolder(Integer.parseInt(BuildInConnectionProperty.STATEMENT_MAX_IDLE.getValue(properties)));
        this.holder.setTimeout(connectionTimeout());
    }

    public String handleDatabase(String dbName) {
        if (null == dbName || SwiftJdbcConstants.EMPTY.equals(dbName.trim())) {
            return SwiftJdbcConstants.DEFAULT_DATABASE;
        }
        return dbName.startsWith(SwiftJdbcConstants.SEPARATOR) ? dbName.substring(1).toUpperCase() : dbName.toUpperCase();
    }

    public ConnectionConfig getConfig() {
        return new ConnectionConfigImpl();
    }

    @Override
    public Statement createStatement() {
        SwiftStatement statement = holder.getIdle();
        if (null == statement) {
            String queryAddress = driver.holder.nextAnalyse();
            JdbcExecutor executor = createJdbcExecutor(queryAddress);
            String maintainAddress = driver.holder.nextRealTime();
            if (queryAddress.equals(maintainAddress)) {
                statement = new SwiftStatementImpl(this, executor, executor);
            } else {
                statement = new SwiftStatementImpl(this, executor, createJdbcExecutor(maintainAddress));
            }
        } else {
            statement.reset();
        }
        holder.using(statement);
        return statement;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) {
        SwiftStatement statement = holder.getPreparedIdle(sql);
        if (null == statement) {
            String queryAddress = driver.holder.nextAnalyse();
            JdbcExecutor executor = createJdbcExecutor(queryAddress);
            String maintainAddress = driver.holder.nextRealTime();
            if (queryAddress.equals(maintainAddress)) {
                statement = new SwiftPreparedStatement(this, sql, executor, executor);
            } else {
                statement = new SwiftPreparedStatement(this, sql, executor, createJdbcExecutor(maintainAddress));
            }
        } else {
            statement.reset();
        }
        holder.using(statement);
        return (PreparedStatement) statement;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return new SwiftDataBaseMetaData(this);
    }

    @Override
    public CallableStatement prepareCall(String sql) {
        return null;
    }

    @Override
    public String nativeSQL(String sql) {
        return null;
    }

    @Override
    public boolean getAutoCommit() {
        return false;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) {

    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }

    @Override
    public void close() throws SQLException {
        this.holder.closeAll();
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void setReadOnly(boolean readOnly) {

    }

    @Override
    public String getCatalog() {
        return database;
    }

    @Override
    public void setCatalog(String catalog) {

    }

    @Override
    public int getTransactionIsolation() {
        return 0;
    }

    @Override
    public void setTransactionIsolation(int level) {

    }

    @Override
    public SQLWarning getWarnings() {
        return null;
    }

    @Override
    public void clearWarnings() {

    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) {
        return null;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() {
        return null;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) {

    }

    @Override
    public int getHoldability() {
        return 0;
    }

    @Override
    public void setHoldability(int holdability) {

    }

    @Override
    public Savepoint setSavepoint() {
        return null;
    }

    @Override
    public Savepoint setSavepoint(String name) {
        return null;
    }

    @Override
    public void rollback(Savepoint savepoint) {

    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) {

    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) {
        return null;
    }

    @Override
    public Clob createClob() {
        return null;
    }

    @Override
    public Blob createBlob() {
        return null;
    }

    @Override
    public NClob createNClob() {
        return null;
    }

    @Override
    public SQLXML createSQLXML() {
        return null;
    }

    @Override
    public boolean isValid(int timeout) {
        return false;
    }

    @Override
    public void setClientInfo(String name, String value) {

    }

    @Override
    public String getClientInfo(String name) {
        return null;
    }

    @Override
    public Properties getClientInfo() {
        return null;
    }

    @Override
    public void setClientInfo(Properties properties) {

    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) {
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) {
        return null;
    }

    @Override
    public String getSchema() {
        return null;
    }

    @Override
    public void setSchema(String schema) {

    }

    @Override
    public void abort(Executor executor) {

    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) {

    }

    @Override
    public int getNetworkTimeout() {
        return 0;
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }

    Object executeQueryInternal(RequestInfo info, JdbcExecutor executor) throws SQLException {
        ApiResponse response = driver.holder.getRequestService().applyWithRetry(executor, info, 3);
        if (response.isError()) {
            throw Exceptions.sql(response.statusCode(), response.description());
        }
        return response.result();
    }

    protected abstract JdbcExecutor createJdbcExecutor(String host, int port);

    protected abstract JdbcExecutor createJdbcExecutor(String address);

    int connectionTimeout() {
        return Integer.parseInt(BuildInConnectionProperty.CONNECTION_TIMEOUT.getValue(properties));
    }

    public String getUrl() {
        return BuildInConnectionProperty.URL.getValue(properties);
    }

    class ConnectionConfigImpl implements ConnectionConfig {

        @Override
        public String swiftUser() {
            return BuildInConnectionProperty.USERNAME.getValue(properties);
        }

        @Override
        public String swiftPassword() {
            return BuildInConnectionProperty.PASSWORD.getValue(properties);
        }

        @Override
        public String swiftDatabase() {
            return database;
        }

        @Override
        public File kerberosKeytab() {
            String filePath = BuildInConnectionProperty.KEYTAB.getValue(properties);
            if (null == filePath) {
                return null;
            }
            File keytab = new File(filePath);
            if (!keytab.exists() || !keytab.isFile()) {
                throw Exceptions.runtime("The " + BuildInConnectionProperty.KEYTAB.name() + " does not "
                        + " reference a normal, existent file: " + filePath);
            }
            return keytab;
        }

        @Override
        public GrammarChecker grammarChecker() {
            String grammar = BuildInConnectionProperty.GRAMMAR.getValue(properties);
            try {
                Class grammarChecker = getClass().getClassLoader().loadClass(grammar);
                return (GrammarChecker) grammarChecker.newInstance();
            } catch (Exception e) {
                throw Exceptions.runtime("", e);
            }
        }

        @Override
        public String kerberosPrincipal() {
            return BuildInConnectionProperty.PRINCIPAL.getValue(properties);
        }

        @Override
        public JdbcExecutor requestExecutor() {
            String host = driver.holder.getConnectUri().getHost();
            int port = driver.holder.getConnectUri().getPort();
            JdbcExecutor executor = createJdbcExecutor(host, port);
            executor.start();
            return executor;
        }
    }

}
