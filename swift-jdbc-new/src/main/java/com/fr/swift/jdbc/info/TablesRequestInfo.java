package com.fr.swift.jdbc.info;

import com.fr.swift.jdbc.json.annotation.JsonProperty;

/**
 * @author yee
 * @date 2018-12-03
 */
public class TablesRequestInfo extends BaseRequestInfo {
    @JsonProperty(value = "database", require = true)
    private String database;
    @JsonProperty(value = "auth", require = true)
    private String authCode;

    public TablesRequestInfo(String database, String authCode) {
        super(Request.TABLES);
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


