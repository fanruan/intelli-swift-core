package com.fr.swift.jdbc.bean;

import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.db.SwiftDatabase;

import java.util.List;

/**
 * @author yee
 * @date 2018/8/29
 */
public class CreateTableBean {
    SwiftDatabase database;
    String tableName;
    List<Column> columns;

    public SwiftDatabase getDatabase() {
        return database;
    }

    public void setDatabase(SwiftDatabase database) {
        this.database = database;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CreateTableBean that = (CreateTableBean) o;

        if (database != that.database) {
            return false;
        }
        if (tableName != null ? !tableName.equals(that.tableName) : that.tableName != null) {
            return false;
        }
        return columns != null ? columns.equals(that.columns) : that.columns == null;
    }

    @Override
    public int hashCode() {
        int result = database != null ? database.hashCode() : 0;
        result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
        result = 31 * result + (columns != null ? columns.hashCode() : 0);
        return result;
    }
}
