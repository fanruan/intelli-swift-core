package com.fr.swift.jdbc.info;

import com.fr.swift.jdbc.json.annotation.JsonProperty;

/**
 * @author yee
 * @date 2018/11/20
 */
public class DeleteSqlInfo extends BaseSqlInfo {
    @JsonProperty("where")
    private String where;

    public DeleteSqlInfo(String sql, String authCode) {
        super(sql, authCode);
        setRequest(Request.DELETE);
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }
}
