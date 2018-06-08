package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.QueryType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/6/3.
 */
public abstract class AbstractQueryBean implements QueryBean {

    @JsonProperty
    private String queryId;
    @JsonProperty
    protected QueryType queryType;

    public abstract QueryType getQueryType();

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }
}
