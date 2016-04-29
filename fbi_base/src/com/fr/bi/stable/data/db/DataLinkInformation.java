package com.fr.bi.stable.data.db;

import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.json.JSONObject;
import com.fr.json.JSONParser;
import com.fr.stable.StringUtils;


/**
 * Created with IntelliJ IDEA.
 * User: Sheldon
 * Date: 13-10-30
 * Time: 下午12:43
 * To change this template use File | Settings | File Templates.
 */
public class DataLinkInformation implements JSONParser {
    private String database;
    private String driver;
    private String url;
    private String user;
    private String password;
    private String originalCharsetName;
    private String newCharsetName;

    public DataLinkInformation() throws Exception {

    }

    /**
     * 通过当前数据获取连接
     *
     * @return 数据连接
     */
    public JDBCDatabaseConnection createJDBCDatabaseConnection() {
        JDBCDatabaseConnection jdbcDatabaseConnection = new JDBCDatabaseConnection(driver, url, user, password);

        if (StringUtils.isNotEmpty(originalCharsetName)) {
            jdbcDatabaseConnection.setOriginalCharsetName(originalCharsetName);
        }
        if (StringUtils.isNotBlank(newCharsetName)) {
            jdbcDatabaseConnection.setNewCharsetName(newCharsetName);
        }

        return jdbcDatabaseConnection;
    }

    /**
     * parse对象
     *
     * @param jo 对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if(jo.has("database")) {
            database = jo.getString("database");
        }
        if (jo.has("driver")) {
            driver = jo.getString("driver");
        }
        if (jo.has("url")) {
            url = jo.getString("url");
        }
        if (jo.has("user")) {
            user = jo.getString("user");
        }
        if (jo.has("password")) {
            password = jo.getString("password");
        }
        if (jo.has("originalCharsetName")) {
            originalCharsetName = jo.getString("originalCharsetName");
        }
        if (jo.has("newCharsetName")) {
            newCharsetName = jo.getString("newCharsetName");
        }
    }
}