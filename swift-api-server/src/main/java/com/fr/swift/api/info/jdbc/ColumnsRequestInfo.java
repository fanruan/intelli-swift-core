package com.fr.swift.api.info.jdbc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.info.BaseRequestInfo;
import com.fr.swift.api.info.RequestType;

/**
 * @author yee
 * @date 2018-12-03
 */
public class ColumnsRequestInfo extends BaseRequestInfo<JdbcRequestParserVisitor> {
    @JsonProperty(value = "swiftUser")
    private String swiftUser;
    @JsonProperty(value = "table")
    private String table;
//    @JsonProperty(value = "auth")
//    private String authCode;

    public ColumnsRequestInfo() {
        super(RequestType.COLUMNS);
    }

    public ColumnsRequestInfo(String swiftUser, String table) {
        super(RequestType.COLUMNS);
        this.swiftUser = swiftUser;
        this.table = table;
//        this.authCode = authCode;
    }

    public String getDatabase() {
        return swiftUser;
    }

    public String getTable() {
        return table;
    }

//    @Override
//    public String getAuthCode() {
//        return authCode;
//    }
//
//    public void setAuthCode(String authCode) {
//        this.authCode = authCode;
//    }

    @Override
    public ApiInvocation accept(JdbcRequestParserVisitor visitor) {
        return visitor.visit(this);
    }
}
