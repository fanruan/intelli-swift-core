package com.fr.swift.jdbc.adaptor.bean;

import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;

/**
 * Created by lyon on 2018/12/10.
 */
public class DeletionBean {

    private String schema;
    private String tableName;
    // TODO: 2018/12/10 FilterInfoBean依赖问题
    private FilterInfoBean filter;

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

    public FilterInfoBean getFilter() {
        return filter;
    }

    public void setFilter(FilterInfoBean filter) {
        this.filter = filter;
    }
}
