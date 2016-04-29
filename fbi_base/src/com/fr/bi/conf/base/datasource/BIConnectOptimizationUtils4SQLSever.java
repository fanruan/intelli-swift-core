package com.fr.bi.conf.base.datasource;

import com.fr.data.impl.JDBCDatabaseConnection;

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
        String url = connection.getURL();
        String optimizedUrl = optimizeSelectMethod(url);
        connection.setURL(optimizedUrl);
        return connection;
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