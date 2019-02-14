package com.fr.swift.query.info.bean.query;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.query.query.QueryType;

import java.util.Set;

/**
 * Created by Lyon on 2018/6/3.
 */
public abstract class AbstractQueryInfoBean implements QueryInfoBean {

    @JsonProperty
    protected QueryType queryType;
    @JsonProperty
    private int fetchSize = 200;
    @JsonProperty
    private String queryId;
    @JsonProperty
    private Set<String> segments;

    @Override
    public String getQueryId() {
        return queryId;
    }

    @Override
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

    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    @Override
    public Set<String> getSegments() {
        return segments;
    }

    @Override
    public void setSegments(Set<String> segments) {
        this.segments = segments;
    }
}
