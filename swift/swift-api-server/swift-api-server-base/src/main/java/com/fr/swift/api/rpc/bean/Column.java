package com.fr.swift.api.rpc.bean;


import com.fr.swift.base.json.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018/8/26
 */
public class Column implements Serializable {
    private static final long serialVersionUID = 3899734575575468524L;
    @JsonProperty(value = "columnName")
    private String columnName;
    @JsonProperty(value = "columnType")
    private int columnType;

    /**
     * Column
     *
     * @param columnName
     * @param columnType
     * @see java.sql.Types
     */
    public Column(String columnName, int columnType) {
        this.columnName = columnName;
        this.columnType = columnType;
    }

    public Column() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Column column = (Column) o;

        if (columnType != column.columnType) {
            return false;
        }
        return columnName != null ? columnName.equals(column.columnName) : column.columnName == null;
    }

    @Override
    public int hashCode() {
        int result = columnName != null ? columnName.hashCode() : 0;
        result = 31 * result + columnType;
        return result;
    }
}
