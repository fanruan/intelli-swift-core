package com.fr.swift.jdbc.adaptor.bean;

import com.fr.swift.query.info.bean.query.QueryInfoBean;

/**
 * Created by lyon on 2018/12/12.
 */
public class SelectionBean {

    private String schema;
    private String tableName;
    private QueryInfoBean queryInfoBean;

    public SelectionBean(String schema, String tableName, QueryInfoBean queryInfoBean) {
        this.schema = schema;
        this.tableName = tableName;
        this.queryInfoBean = queryInfoBean;
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

    public QueryInfoBean getQueryInfoBean() {
        return queryInfoBean;
    }

    public void setQueryInfoBean(QueryInfoBean queryInfoBean) {
        this.queryInfoBean = queryInfoBean;
    }
}
