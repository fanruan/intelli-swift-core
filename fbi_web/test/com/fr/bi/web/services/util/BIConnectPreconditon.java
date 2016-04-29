package com.fr.bi.web.services.util;

import com.fr.data.impl.JDBCDatabaseConnection;

/**
 * Created by Connery on 2014/11/17.
 */
public class BIConnectPreconditon {

    private JDBCDatabaseConnection connectionSQLServer_true;
    private JDBCDatabaseConnection connectionSQLServer_null;
    private JDBCDatabaseConnection connectionSQLServer_zone;
    private JDBCDatabaseConnection connectionSQLServer_false;
    private JDBCDatabaseConnection connectionSQLServer_URLWithoutSemicolon;
    private JDBCDatabaseConnection connectionSQLServer_WithSelectMethod;

    public BIConnectPreconditon() {
        connectionSQLServer_true = new JDBCDatabaseConnection();
        connectionSQLServer_null = new JDBCDatabaseConnection();
        connectionSQLServer_zone = new JDBCDatabaseConnection();
        connectionSQLServer_false = new JDBCDatabaseConnection();
        connectionSQLServer_URLWithoutSemicolon = new JDBCDatabaseConnection();
        connectionSQLServer_WithSelectMethod = new JDBCDatabaseConnection();
    }

    /**
     * 获得数据正确的SqlServer
     */
    public JDBCDatabaseConnection getConnectionSQLServer_true() {
        return connectionSQLServer_true;
    }

    /**
     * 获得数据null的SqlServer
     */
    public JDBCDatabaseConnection getConnectionSQLServer_null() {
        return connectionSQLServer_null;
    }

    /**
     * 获得数据空的SqlServer
     */
    public JDBCDatabaseConnection getConnectionSQLServer_zone() {
        return connectionSQLServer_zone;
    }

    /**
     * 获得数据错误的SqlServer
     */
    public JDBCDatabaseConnection getConnectionSQLServer_false() {
        return connectionSQLServer_false;
    }

    /**
     * 获得数据URl没有分号的SqlServer
     */
    public JDBCDatabaseConnection getConnectionSQLServer_URLWithoutSemicolon() {
        return connectionSQLServer_URLWithoutSemicolon;
    }

    /**
     * 获得数据没有SelectMethod的SqlServer
     */
    public JDBCDatabaseConnection getConnectionSQLServer_WithSelectMethod() {
        return connectionSQLServer_WithSelectMethod;
    }

    /**
     * 复位
     */
    public void reset() {

        connectionSQLServer_true.setDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connectionSQLServer_null.setDriver(null);
        connectionSQLServer_zone.setDriver("");
        connectionSQLServer_false.setDriver("s");

        connectionSQLServer_true.setURL("jdbc:sqlserver://192.168.100.74:1433;databaseName=jr;");
        connectionSQLServer_null.setURL(null);
        connectionSQLServer_zone.setURL("");
        connectionSQLServer_false.setURL("jdbc:sqlserver://192.168.100.74:1433;databaseName=jr;;;;  ;;;;");
        connectionSQLServer_URLWithoutSemicolon.setURL("jdbc:sqlserver://192.168.100.74:1433;databaseName=jr");
        connectionSQLServer_WithSelectMethod.setURL("jdbc:sqlserver://192.168.100.74:1433;databaseName=jr;selectMethod=direct");
    }
}