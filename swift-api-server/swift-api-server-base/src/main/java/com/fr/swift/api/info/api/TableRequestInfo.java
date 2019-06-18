package com.fr.swift.api.info.api;

import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.info.BaseRequestInfo;
import com.fr.swift.api.info.RequestType;
import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.db.SwiftSchema;

/**
 * @author yee
 * @date 2018-12-07
 */
public class TableRequestInfo extends BaseRequestInfo<ApiRequestParserVisitor> {
    @JsonProperty(value = "auth")
    private String authCode;
    @JsonProperty(value = "database")
    private SwiftSchema database;
    @JsonProperty(value = "table")
    private String table;

    protected TableRequestInfo(RequestType request) {
        super(request);
    }

    @Override
    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public SwiftSchema getDatabase() {
        return database;
    }

    public void setDatabase(SwiftSchema database) {
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
