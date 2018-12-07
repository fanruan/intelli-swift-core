package com.fr.swift.jdbc.info;

import com.fr.swift.api.info.BaseRequestInfo;
import com.fr.swift.api.json.annotation.ApiJsonProperty;

/**
 * @author yee
 * @date 2018-12-03
 */
public class TablesRequestInfo extends BaseRequestInfo {
    @ApiJsonProperty(value = "database", require = true)
    private String database;
    @ApiJsonProperty(value = "auth", require = true)
    private String authCode;

    public TablesRequestInfo(String database, String authCode) {
        super(JdbcRequestType.TABLES);
        this.database = database;
        this.authCode = authCode;
    }

    public String getDatabase() {
        return database;
    }

    @Override
    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}


