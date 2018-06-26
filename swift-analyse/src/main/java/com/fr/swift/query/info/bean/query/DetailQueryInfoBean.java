package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.query.QueryType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Lyon on 2018/6/3.
 */
public class DetailQueryInfoBean extends AbstractSingleTableQueryInfoBean {

    @JsonProperty
    private List<String> columns;

    @Override
    public QueryType getQueryType() {
        return QueryType.DETAIL;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
}
