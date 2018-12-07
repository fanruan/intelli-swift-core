package com.fr.swift.jdbc.info;

import com.fr.swift.api.info.BaseRequestInfo;
import com.fr.swift.api.json.annotation.ApiJsonProperty;

/**
 * @author yee
 * @date 2018/11/20
 */
public class SqlRequestInfo extends BaseRequestInfo {
    @ApiJsonProperty(value = "sql", require = true)
    protected String sql;
    @ApiJsonProperty(value = "auth", require = true)
    protected String authCode;
    @ApiJsonProperty(value = "database", require = true)
    protected String database;


    public SqlRequestInfo(String sql) {
        super(JdbcRequestType.SQL);
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
