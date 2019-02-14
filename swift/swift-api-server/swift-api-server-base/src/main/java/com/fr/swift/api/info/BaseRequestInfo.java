package com.fr.swift.api.info;

import com.fr.swift.base.json.annotation.JsonProperty;

import java.util.UUID;

/**
 * @author yee
 * @date 2018-12-03
 */
public abstract class BaseRequestInfo<T extends RequestParserVisitor> implements RequestInfo<T> {
    @JsonProperty(value = "requestType", serializeMethod = "name")
    protected RequestInfo.Request request;
    @JsonProperty(value = "requestId")
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
    public <R extends Request> R getRequest() {
        return (R) request;
    }
}
