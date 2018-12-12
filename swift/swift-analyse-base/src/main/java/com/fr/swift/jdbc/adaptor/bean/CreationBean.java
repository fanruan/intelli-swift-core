package com.fr.swift.jdbc.adaptor.bean;

import java.util.List;

/**
 * Created by lyon on 2018/12/10.
 */
public class CreationBean {

    private String schema;
    private String tableName;
    private List<ColumnBean> fields;
    private Object partition;

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

    public List<ColumnBean> getFields() {
        return fields;
    }

    public void setFields(List<ColumnBean> fields) {
        this.fields = fields;
    }

    public Object getPartition() {
        return partition;
    }

    public void setPartition(Object partition) {
        this.partition = partition;
    }
}
