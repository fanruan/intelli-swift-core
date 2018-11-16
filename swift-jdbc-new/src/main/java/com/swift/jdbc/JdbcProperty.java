package com.swift.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author yee
 * @date 2018/11/16
 */
public final class JdbcProperty {
    private static JdbcProperty property;

    static {
        load();
    }

    private String connectionSchema;
    private int majorVersion;
    private int minorVersion;
    private long connectionTimeout;
    private int connectionMaxIdle;
    private int statementMaxIdle;
    private boolean compliant;

    public JdbcProperty(
            String connectionSchema,
            int majorVersion,
            int minorVersion,
            long connectionTimeout,
            int connectionMaxIdle,
            int statementMaxIdle,
            boolean compliant) {
        this.connectionSchema = connectionSchema;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.connectionTimeout = connectionTimeout;
        this.connectionMaxIdle = connectionMaxIdle;
        this.statementMaxIdle = statementMaxIdle;
        this.compliant = compliant;
    }

    public static JdbcProperty get() {
        return property;
    }

    private static void load() {
        InputStream is = JdbcProperty.class.getClassLoader().getResourceAsStream("com-fr-swift-jdbc.properties");
        if (null == is) {
            property = new JdbcProperty(
                    "jdbc:swift",
                    10,
                    1,
                    3000,
                    50,
                    200,
                    false
            );
            return;
        }
        Properties properties = new Properties();
        try {
            properties.load(is);
            String connectionSchema = properties.getProperty("connection.schema", "jdbc:swift");
            int majorVersion = Integer.parseInt(properties.getProperty("driver.version.major", "10"));
            int minorVersion = Integer.parseInt(properties.getProperty("driver.version.minor", "1"));
            long timeout = Long.parseLong(properties.getProperty("connection.timeout", "30000"));
            int connectionMaxIdle = Integer.parseInt(properties.getProperty("connection.maxIdle", "50"));
            int statementMaxIdle = Integer.parseInt(properties.getProperty("statement.maxIdle", "200"));
            boolean compliant = Boolean.parseBoolean(properties.getProperty("driver.compliant", "false"));
            property = new JdbcProperty(
                    connectionSchema,
                    majorVersion,
                    minorVersion,
                    timeout,
                    connectionMaxIdle,
                    statementMaxIdle,
                    compliant);
        } catch (IOException e) {
            property = new JdbcProperty(
                    "jdbc:swift",
                    10,
                    1,
                    3000,
                    50,
                    200,
                    false
            );
        } finally {
            try {
                is.close();
            } catch (IOException ignore) {
            }
        }
    }

    public String getConnectionSchema() {
        return connectionSchema;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public boolean isCompliant() {
        return compliant;
    }

    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getConnectionMaxIdle() {
        return connectionMaxIdle;
    }

    public int getStatementMaxIdle() {
        return statementMaxIdle;
    }
}
