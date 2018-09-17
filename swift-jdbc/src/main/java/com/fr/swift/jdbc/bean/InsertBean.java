package com.fr.swift.jdbc.bean;

import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.source.Row;

import java.util.List;

/**
 * @author yee
 * @date 2018/8/26
 */
public class InsertBean {
    private String tableName;
    private SwiftDatabase schema;
    private List<String> columnNames;
    private List<Row> datas;
    private String queryJson;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public SwiftDatabase getSchema() {
        return schema;
    }

    public void setSchema(SwiftDatabase schema) {
        this.schema = schema;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public List<Row> getDatas() {
        return datas;
    }

    public void setDatas(List<Row> datas) {
        this.datas = datas;
    }

    public String getQueryJson() {
        return queryJson;
    }

    public void setQueryJson(String queryJson) {
        this.queryJson = queryJson;
    }
}
