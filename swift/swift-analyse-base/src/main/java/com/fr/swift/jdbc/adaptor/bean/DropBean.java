package com.fr.swift.jdbc.adaptor.bean;

/**
 * Created by lyon on 2018/12/10.
 */
public class DropBean {

    private String schema;
    private String tableName;

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
}
