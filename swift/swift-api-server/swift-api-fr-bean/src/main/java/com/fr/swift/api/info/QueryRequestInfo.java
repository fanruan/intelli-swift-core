package com.fr.swift.api.info;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.db.SwiftDatabase;

/**
 * @author yee
 * @date 2018-12-07
 */
public class QueryRequestInfo extends BaseRequestInfo<ApiRequestParserVisitor> {
    @JsonProperty(value = "auth")
    private String authCode;

    @JsonProperty(value = "queryJson")
    private String queryJson;

    @JsonProperty("database")
    private SwiftDatabase database;

    public QueryRequestInfo() {
        super(ApiRequestType.JSON_QUERY);
    }

    @Override
    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getQueryJson() {
        return queryJson;
    }

    public void setQueryJson(String queryJson) {
        this.queryJson = queryJson;
    }

    public SwiftDatabase getDatabase() {
        return database;
    }

    public void setDatabase(SwiftDatabase database) {
        this.database = database;
    }

    @Override
    public ApiInvocation accept(ApiRequestParserVisitor visitor) {
        return visitor.visit(this);
    }
}
