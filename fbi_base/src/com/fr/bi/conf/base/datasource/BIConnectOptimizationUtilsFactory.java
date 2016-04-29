package com.fr.bi.conf.base.datasource;

import com.fr.data.impl.JDBCDatabaseConnection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Connery on 2014/11/17.
 */
public class BIConnectOptimizationUtilsFactory {
    private static final int DEFALUT_UTILS_ID = 0x00;
    private static final int SQLSEVER_UTILS_ID = 0x01;
    private static final int MYSQL_UTILS_ID = 0x02;
    private static final String SQLSEVER_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    private static Map<String, Integer> map = new HashMap<String, Integer>() {{
        put(SQLSEVER_DRIVER, SQLSEVER_UTILS_ID);
        put(MYSQL_DRIVER, MYSQL_UTILS_ID);
    }};

    /**
     * 依据driver，获得对应的连接优化工具
     *
     * @param connection 连接信息
     * @return
     */
    public static BIConnectOptimizationUtils getOptimizationUtils(JDBCDatabaseConnection connection) {
        String driver = connection.getDriver();
        if (driver != null) {
            if (map.containsKey(driver)) {
                return generate(map.get(driver));
            }
        }
        return generate(DEFALUT_UTILS_ID);
    }

    private static BIConnectOptimizationUtils generate(int id) {
        BIConnectOptimizationUtils utils;
        switch (id) {
            case SQLSEVER_UTILS_ID:
                utils = new BIConnectOptimizationUtils4SQLSever();
                break;
            default:
                utils = new BIConnectOptimizationUtils();
        }
        return utils;
    }
}