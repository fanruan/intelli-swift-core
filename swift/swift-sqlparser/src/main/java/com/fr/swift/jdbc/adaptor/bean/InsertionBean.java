package com.fr.swift.jdbc.adaptor.bean;

import java.util.List;

/**
 * Created by lyon on 2018/12/10.
 */
public class InsertionBean {

    private String schema;
    private String tableName;
    private List<String> fields;
    private List<List> rows;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public List<List> getRows() {
        return rows;
    }

    public void setRows(List<List> rows) {
        this.rows = rows;
    }
}
