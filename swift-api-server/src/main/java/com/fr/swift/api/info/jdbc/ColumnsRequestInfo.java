package com.fr.swift.api.info.jdbc;

import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.info.BaseRequestInfo;
import com.fr.swift.api.info.RequestType;
import com.fr.swift.base.json.annotation.JsonProperty;

/**
 * @author yee
 * @date 2018-12-03
 */
public class ColumnsRequestInfo extends BaseRequestInfo<JdbcRequestParserVisitor> {
    @JsonProperty(value = "database")
    private String database;
    @JsonProperty(value = "table")
    private String table;
    @JsonProperty(value = "auth")
    private String authCode;

    public ColumnsRequestInfo() {
        super(RequestType.COLUMNS);
    }

    public ColumnsRequestInfo(String database, String table, String authCode) {
        super(RequestType.COLUMNS);
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

    @Override
    public ApiInvocation accept(JdbcRequestParserVisitor visitor) {
        return visitor.visit(this);
    }
}
