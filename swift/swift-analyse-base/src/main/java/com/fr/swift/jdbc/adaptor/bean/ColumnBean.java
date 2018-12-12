package com.fr.swift.jdbc.adaptor.bean;

/**
 * Created by lyon on 2018/12/11.
 */
public class ColumnBean {

    private String columnName;
    private int columnType;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getColumnType() {
        return columnType;
    }

    public void setColumnType(int columnType) {
        this.columnType = columnType;
    }
}
