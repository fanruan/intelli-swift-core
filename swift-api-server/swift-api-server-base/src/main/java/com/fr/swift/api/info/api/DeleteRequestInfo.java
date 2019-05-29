package com.fr.swift.api.info.api;

import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.info.RequestType;
import com.fr.swift.base.json.annotation.JsonProperty;

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
