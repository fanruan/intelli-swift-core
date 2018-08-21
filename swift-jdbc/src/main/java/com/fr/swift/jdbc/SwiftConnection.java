package com.fr.swift.jdbc;

import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.jdbc.exception.SwiftJDBCNotSupportedException;
import com.fr.swift.util.Crasher;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
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
 * Created by pony on 2018/8/17.
 */
public class SwiftConnection implements java.sql.Connection {
    //先写死，只能查平台的表
    private SwiftDatabase.Schema SCHEMA = SwiftDatabase.Schema.DECISION_LOG;
    @Override
    public Statement createStatement() throws SQLException {
        return new SwiftStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {

    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return false;
    }

    @Override
    public void commit() throws SQLException {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void rollback() throws SQLException {

    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return new SwiftDatabaseMetadata(SCHEMA);
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public String getCatalog() throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void clearWarnings() throws SQLException {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return new SwiftStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public int getHoldability() throws SQLException {
        //先随便return一个，暂时没什么用
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Clob createClob() throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Blob createBlob() throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public NClob createNClob() throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return false;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    public void setSchema(String schema) throws SQLException {

    }

    public String getSchema() throws SQLException {
        return SCHEMA.getName();
    }

    public void abort(Executor executor) throws SQLException {

    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
