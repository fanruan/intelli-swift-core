package com.fr.bi.conf.base.datasource;

import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.general.ComparatorUtils;

/**
 * Created by Connery on 2014/11/17.
 */
public class BIConnectOptimizationUtils4SQLSever extends BIConnectOptimizationUtils {
    private static final String SELECT_METHOD = "selectmethod";

    /**
     * 优化连接信息
     *
     * @param connection 连接信息
     * @return 优化过的连接信息
     */
    @Override
    public JDBCDatabaseConnection optimizeConnection(JDBCDatabaseConnection connection) {
        JDBCDatabaseConnection deepCloneConnection = new JDBCDatabaseConnection();//新建deepCloneConnection的原因是不改变datasourceManager中connection属性
        //cube取数且为sqlserver连接，需要特殊处理
        deepCloneConnection.setDbcpAttr(connection.getDbcpAttr());
        deepCloneConnection.setDriver(connection.getDriver());
        deepCloneConnection.setEncryptPassword(connection.isEncryptPassword());
        deepCloneConnection.setPassword(connection.getPassword());
        deepCloneConnection.setURL(optimizeSelectMethod(connection.getURL()));//sqlserver连接url，如果没有有selectMethod字段，增加该字段
        deepCloneConnection.setUser(connection.getUser());
        deepCloneConnection.setNewCharsetName(connection.getNewCharsetName());
        deepCloneConnection.setOriginalCharsetName(connection.getOriginalCharsetName());
        return deepCloneConnection;
    }

    /**
     * if url does not contain "select method = cursor",
     * append the select method string to the end of the url
     */
    private String optimizeSelectMethod(String url) {
        if (url != null && !url.isEmpty()) {
            String lowerURl = url.toLowerCase();

            if (!lowerURl.contains(SELECT_METHOD)) {
                //cut off all semicolons and spaces at the tail
                while (url.charAt(url.length() - 1) == ';' || url.charAt(url.length() - 1) == ' ') {
                    url = url.substring(0, url.length() - 1);
                }
                StringBuffer sb = new StringBuffer(url);
                sb.append(";selectMethod=cursor");
                return sb.toString();
            }
        } else {
        }
        return url;
    }
}