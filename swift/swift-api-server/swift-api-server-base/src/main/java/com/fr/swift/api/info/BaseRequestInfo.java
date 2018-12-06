package com.fr.swift.api.info;

import com.fr.swift.api.json.annotation.ApiJsonProperty;

import java.util.UUID;

/**
 * @author yee
 * @date 2018-12-03
 */
public class BaseRequestInfo implements RequestInfo {
    @ApiJsonProperty(value = "requestType", require = true)
    protected RequestInfo.Request request;
    @ApiJsonProperty(value = "requestId", require = true)
    private String requestId;

    public BaseRequestInfo(RequestInfo.Request request) {
        this.request = request;
        this.requestId = UUID.randomUUID().toString();
    }

    public String getRequestId() {
        return requestId;
    }

    @Override
    public String getAuthCode() {
        return "";
    }

    @Override
    public RequestInfo.Request getRequest() {
        return request;
    }
}
