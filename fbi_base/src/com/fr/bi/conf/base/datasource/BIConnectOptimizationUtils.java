package com.fr.bi.conf.base.datasource;

import com.fr.data.impl.JDBCDatabaseConnection;

/**
 * Created by Connery on 2014/11/17.
 */
public class BIConnectOptimizationUtils {
    /**
     * 优化SQLSERVER连接信息
     *
     * @param connection 连接信息
     * @return 优化过的连接信息
     */
    public JDBCDatabaseConnection optimizeConnection(JDBCDatabaseConnection connection) {
        return connection;
    }
}