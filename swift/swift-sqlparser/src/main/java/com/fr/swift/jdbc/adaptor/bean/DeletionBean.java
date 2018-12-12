package com.fr.swift.jdbc.adaptor.bean;

/**
 * Created by lyon on 2018/12/10.
 */
public class DeletionBean {

    private String schema;
    private String tableName;
    // TODO: 2018/12/10 FilterInfoBean依赖问题
    private Object filter;

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

    public Object getFilter() {
        return filter;
    }

    public void setFilter(Object filter) {
        this.filter = filter;
    }
}
