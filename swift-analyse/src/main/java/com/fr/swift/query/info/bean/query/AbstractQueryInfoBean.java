package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.query.QueryType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.util.Set;

/**
 * Created by Lyon on 2018/6/3.
 */
public abstract class AbstractQueryInfoBean implements QueryInfoBean {

    @JsonProperty
    private String queryId;
    @JsonProperty
    protected QueryType queryType;
    @JsonProperty
    protected Set<URI> querySegment;

    @Override
    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    @Override
    public QueryType getQueryType() {
        return queryType;
    }

    @Override
    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public Set<URI> getQuerySegment() {
        return querySegment;
    }

    @Override
    public void setQuerySegment(Set<URI> querySegment) {
        this.querySegment = querySegment;
    }
}
