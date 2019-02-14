package com.fr.swift.api.info;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.db.SwiftDatabase;

/**
 * @author yee
 * @date 2018-12-07
 */
public class TableRequestInfo extends BaseRequestInfo<ApiRequestParserVisitor> {
    @JsonProperty(value = "auth")
    private String authCode;
    @JsonProperty(value = "database")
    private SwiftDatabase database;
    @JsonProperty(value = "table")
    private String table;

    public TableRequestInfo(Request request) {
        super(request);
    }

    public TableRequestInfo() {
        super(null);
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

    @Override
    public ApiInvocation accept(ApiRequestParserVisitor visitor) {
        return visitor.visit(this);
    }
}
