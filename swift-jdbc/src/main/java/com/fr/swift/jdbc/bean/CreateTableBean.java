package com.fr.swift.jdbc.bean;

import com.fr.general.ComparatorUtils;
import com.fr.stable.AssistUtils;
import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.db.SwiftDatabase;

import java.util.List;

/**
 * @author yee
 * @date 2018/8/29
 */
public class CreateTableBean {
    private SwiftDatabase database;
    private String tableName;
    private List<Column> columns;

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
    public boolean equals(Object obj) {
        return obj instanceof CreateTableBean
                && AssistUtils.equals(this.database, ((CreateTableBean) obj).database)
                && AssistUtils.equals(this.tableName, ((CreateTableBean) obj).tableName)
                && ComparatorUtils.equals(this.columns, ((CreateTableBean) obj).columns);
    }

    @Override
    public int hashCode() {
        return AssistUtils.hashCode(this.database, this.tableName, this.columns);
    }
}
