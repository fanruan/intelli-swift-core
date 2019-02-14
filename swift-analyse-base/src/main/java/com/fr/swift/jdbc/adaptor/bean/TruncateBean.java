package com.fr.swift.jdbc.adaptor.bean;

/**
 * @author yee
 * @date 2019-01-15
 */
public class TruncateBean {
    private String schema;
    private String tableName;

    public TruncateBean(String schema, String tableName) {
        this.schema = schema;
        this.tableName = tableName;
    }

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
