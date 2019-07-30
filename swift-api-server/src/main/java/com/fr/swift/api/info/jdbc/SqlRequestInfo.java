package com.fr.swift.api.info.jdbc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.info.BaseRequestInfo;
import com.fr.swift.api.info.RequestType;

/**
 * @author yee
 * @date 2018/11/20
 */
public class SqlRequestInfo extends BaseRequestInfo<JdbcRequestParserVisitor> {
    @JsonProperty(value = "sql")
    protected String sql;
    @JsonProperty(value = "auth")
    protected String authCode;
    @JsonProperty(value = "database")
    protected String database;
    @JsonIgnore
    private boolean select;

    public SqlRequestInfo() {
        super(RequestType.SQL);
    }

    public SqlRequestInfo(String sql, boolean select) {
        super(RequestType.SQL);
        this.sql = sql;
        this.select = select;
    }

    public String getSql() {
        return sql;
    }

    @Override
    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public boolean isSelect() {
        return select;
    }

    @Override
    public ApiInvocation accept(JdbcRequestParserVisitor visitor) {
        return visitor.visit(this);
    }
}
