package com.fr.swift.api.info.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.info.BaseRequestInfo;
import com.fr.swift.api.info.RequestType;
import com.fr.swift.db.SwiftSchema;

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
    private SwiftSchema database;

    public QueryRequestInfo() {
        super(RequestType.JSON_QUERY);
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

    public SwiftSchema getDatabase() {
        return database;
    }

    public void setDatabase(SwiftSchema database) {
        this.database = database;
    }

    @Override
    public ApiInvocation accept(ApiRequestParserVisitor visitor) {
        return visitor.visit(this);
    }
}
