package com.fr.swift.jdbc.info;

import com.fr.swift.jdbc.json.annotation.JsonProperty;

import java.util.List;

/**
 * @author yee
 * @date 2018/11/20
 */
public class InsertSqlInfo extends BaseSqlInfo {
    @JsonProperty("columns")
    private List<String> columns;
    @JsonProperty("values")
    private List<List> values;

    public InsertSqlInfo(String sql, String authCode) {
        super(sql, authCode);
        setRequest(Request.INSERT);
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<List> getValues() {
        return values;
    }

    public void setValues(List<List> values) {
        this.values = values;
    }
}
