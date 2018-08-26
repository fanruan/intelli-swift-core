package com.fr.swift.api.rpc.bean;

import com.fr.swift.source.ColumnTypeConstants;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018/8/26
 */
public class Column implements Serializable {
    private static final long serialVersionUID = 3899734575575468524L;
    private String columnName;
    private ColumnTypeConstants.ColumnType columnType;

    public Column(String columnName, ColumnTypeConstants.ColumnType columnType) {
        this.columnName = columnName;
        this.columnType = columnType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public ColumnTypeConstants.ColumnType getColumnType() {
        return columnType;
    }

    public void setColumnType(ColumnTypeConstants.ColumnType columnType) {
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

        if (columnName != null ? !columnName.equals(column.columnName) : column.columnName != null) {
            return false;
        }
        return columnType == column.columnType;
    }

    @Override
    public int hashCode() {
        int result = columnName != null ? columnName.hashCode() : 0;
        result = 31 * result + (columnType != null ? columnType.hashCode() : 0);
        return result;
    }
}
