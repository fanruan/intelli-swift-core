package com.fr.swift.api.info;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * @author yee
 * @date 2018-12-03
 */
public abstract class BaseRequestInfo<T extends RequestParserVisitor> implements RequestInfo<T> {
    @JsonProperty(value = "requestType")
    protected RequestType requestType;
    @JsonProperty(value = "requestId")
    private String requestId;

    public BaseRequestInfo(RequestType requestType) {
        this.requestType = requestType;
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
    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}
