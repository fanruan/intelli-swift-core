package com.fr.swift.api.info;

import com.fr.swift.api.json.annotation.ApiJsonProperty;

/**
 * @author yee
 * @date 2018-12-07
 */
public class QueryRequestInfo extends BaseRequestInfo {
    @ApiJsonProperty(value = "auth", require = true)
    private String authCode;

    @ApiJsonProperty(value = "queryJson", require = true)
    private String queryJson;

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
}
