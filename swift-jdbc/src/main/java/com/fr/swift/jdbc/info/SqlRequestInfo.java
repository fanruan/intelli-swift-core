package com.fr.swift.jdbc.info;

import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.info.BaseRequestInfo;
import com.fr.swift.api.info.JdbcRequestType;
import com.fr.swift.base.json.annotation.JsonIgnore;
import com.fr.swift.base.json.annotation.JsonProperty;

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
        super(JdbcRequestType.SQL);
    }

    public SqlRequestInfo(String sql, boolean select) {
        super(JdbcRequestType.SQL);
        this.sql = sql;
        this.select = select;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getSql() {
        return sql;
    }

    @Override
    public String getAuthCode() {
        return authCode;
    }

    public String getDatabase() {
        return database;
    }

    public boolean isSelect() {
        return select;
    }

    @Override
    public ApiInvocation accept(JdbcRequestParserVisitor visitor) {
        return visitor.visit(this);
    }
}
