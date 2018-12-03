package com.fr.swift.jdbc.info;

import com.fr.swift.jdbc.json.annotation.JsonProperty;

import java.util.UUID;

/**
 * @author yee
 * @date 2018-12-03
 */
public class BaseRequestInfo implements RequestInfo {
    @JsonProperty(value = "requestType", require = true)
    protected RequestInfo.Request request;
    @JsonProperty(value = "requestId", require = true)
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
