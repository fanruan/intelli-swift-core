package com.fr.swift.jdbc.info;

import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.info.BaseRequestInfo;
import com.fr.swift.api.info.JdbcRequestType;
import com.fr.swift.base.json.annotation.JsonProperty;

/**
 * @author yee
 * @date 2018-12-03
 */
public class TablesRequestInfo extends BaseRequestInfo<JdbcRequestParserVisitor> {
    @JsonProperty(value = "database")
    private String database;
    @JsonProperty(value = "auth")
    private String authCode;

    public TablesRequestInfo() {
        super(JdbcRequestType.TABLES);
    }

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

    @Override
    public ApiInvocation accept(JdbcRequestParserVisitor visitor) {
        return visitor.visit(this);
    }
}


