package com.fr.swift.api.info;

import com.fr.swift.api.json.annotation.ApiJsonProperty;

/**
 * @author yee
 * @date 2018-12-07
 */
public class DeleteTableRequestInfo extends TableRequestInfo {
    @ApiJsonProperty("where")
    private String where;

    public DeleteTableRequestInfo() {
        super(ApiRequestType.DELETE);
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }
}
