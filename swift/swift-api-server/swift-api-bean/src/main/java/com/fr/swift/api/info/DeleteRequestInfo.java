package com.fr.swift.api.info;

import com.fr.swift.base.json.annotation.JsonProperty;

/**
 * @author yee
 * @date 2018-12-07
 */
public class DeleteRequestInfo extends TableRequestInfo {
    @JsonProperty("where")
    private String where;

    public DeleteRequestInfo() {
        super(ApiRequestType.DELETE);
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
