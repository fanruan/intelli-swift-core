package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.query.QueryType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

/**
 * Created by Lyon on 2018/6/3.
 */
public abstract class AbstractQueryInfoBean implements QueryInfoBean {

    @JsonProperty
    private String queryId;
    @JsonProperty
    protected QueryType queryType;
    @JsonProperty
    protected URI querySegment;

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

    public URI getQuerySegment() {
        return querySegment;
    }

    public void setQuerySegment(URI querySegment) {
        this.querySegment = querySegment;
    }
}
