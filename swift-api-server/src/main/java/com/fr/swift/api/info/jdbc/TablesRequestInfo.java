package com.fr.swift.api.info.jdbc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.info.BaseRequestInfo;
import com.fr.swift.api.info.RequestType;

/**
 * @author yee
 * @date 2018-12-03
 */
public class TablesRequestInfo extends BaseRequestInfo<JdbcRequestParserVisitor> {
    @JsonProperty(value = "database")
    private String database;
    @JsonProperty(value = "swiftUser")
    private String swiftUser;

    public TablesRequestInfo() {
        super(RequestType.TABLES);
    }

    public TablesRequestInfo(String database, String swiftUser) {
        super(RequestType.TABLES);
        this.database = database;
        this.swiftUser = swiftUser;
    }

    public String getDatabase() {
        return database;
    }

    @Override
    public String getAuthCode() {
        return swiftUser;
    }

    public void setAuthCode(String userName) {
        this.swiftUser = userName;
    }

    @Override
    public ApiInvocation accept(JdbcRequestParserVisitor visitor) {
        return visitor.visit(this);
    }
}


