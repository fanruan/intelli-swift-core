package com.fr.swift.jdbc.info;

import com.fr.swift.jdbc.json.annotation.JsonProperty;

/**
 * @author yee
 * @date 2018-12-03
 */
public class ColumnsRequestInfo extends BaseRequestInfo {
    @JsonProperty(value = "database", require = true)
    private String database;
    @JsonProperty(value = "table", require = true)
    private String table;
    @JsonProperty(value = "auth", require = true)
    private String authCode;

    public ColumnsRequestInfo(String database, String table, String authCode) {
        super(Request.COLUMNS);
        this.database = database;
        this.table = table;
        this.authCode = authCode;
    }

    public String getDatabase() {
        return database;
    }

    public String getTable() {
        return table;
    }

    @Override
    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}
