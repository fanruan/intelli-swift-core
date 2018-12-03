package com.fr.swift.jdbc.info;

import com.fr.swift.jdbc.json.annotation.JsonProperty;

/**
 * @author yee
 * @date 2018/11/20
 */
public class SqlRequestInfo extends BaseRequestInfo {
    @JsonProperty(value = "sql", require = true)
    protected String sql;
    @JsonProperty(value = "auth", require = true)
    protected String authCode;
    @JsonProperty(value = "database", require = true)
    protected String database;


    public SqlRequestInfo(String sql) {
        super(RequestInfo.Request.SQL);
        this.sql = sql;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getSql() {
        return sql;
    }

    @Override
    public String getAuthCode() {
        return authCode;
    }

    public String getDatabase() {
        return database;
    }
}
