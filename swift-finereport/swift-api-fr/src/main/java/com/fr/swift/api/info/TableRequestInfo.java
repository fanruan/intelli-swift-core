package com.fr.swift.api.info;

import com.fr.swift.api.json.annotation.ApiJsonProperty;
import com.fr.swift.db.SwiftDatabase;

/**
 * @author yee
 * @date 2018-12-07
 */
public class TableRequestInfo extends BaseRequestInfo {
    @ApiJsonProperty(value = "auth", require = true)
    private String authCode;
    @ApiJsonProperty(value = "database", require = true)
    private SwiftDatabase database;
    @ApiJsonProperty(value = "table", require = true)
    private String table;

    public TableRequestInfo(Request request) {
        super(request);
    }


    @Override
    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public SwiftDatabase getDatabase() {
        return database;
    }

    public void setDatabase(SwiftDatabase database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
