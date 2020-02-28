package com.fr.swift.api.info.jdbc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.info.BaseRequestInfo;
import com.fr.swift.api.info.RequestType;

/**
 * @author yee
 * @version 1.1
 * Created by yee on 2019-08-31
 */
public class CatalogRequestInfo extends BaseRequestInfo<JdbcRequestParserVisitor> {
    @JsonProperty(value = "auth")
    private String authCode;

    public CatalogRequestInfo() {
        super(RequestType.CATALOGS);
    }

    @Override
    public ApiInvocation accept(JdbcRequestParserVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}
