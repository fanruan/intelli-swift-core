package com.fr.swift.api.info.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.info.RequestType;

/**
 * @author yee
 * @date 2018-12-07
 */
public class DeleteRequestInfo extends TableRequestInfo {
    @JsonProperty("where")
    private String where;

    public DeleteRequestInfo() {
        super(RequestType.DELETE);
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    @Override
    public ApiInvocation accept(ApiRequestParserVisitor visitor) {
        return visitor.visit(this);
    }
}
