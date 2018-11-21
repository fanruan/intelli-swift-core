package com.fr.swift.jdbc.info;

import com.fr.swift.jdbc.json.annotation.JsonProperty;

/**
 * @author yee
 * @date 2018/11/20
 */
public class BaseSqlInfo implements SqlInfo {
    @JsonProperty(value = "sql", require = true)
    protected String sql;
    @JsonProperty(value = "auth", require = true)
    protected String authCode;
    @JsonProperty("table")
    protected String tableName;
    @JsonProperty(value = "database", require = true)
    protected String database;
    @JsonProperty(value = "requestType", require = true)
    protected Request request;


    public BaseSqlInfo(String sql, String authCode) {
        this.sql = sql;
        this.authCode = authCode;
    }

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public String getAuthCode() {
        return authCode;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    @Override
    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}
