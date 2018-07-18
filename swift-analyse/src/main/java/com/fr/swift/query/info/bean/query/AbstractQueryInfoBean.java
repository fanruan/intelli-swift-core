package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.query.QueryType;
import com.fr.swift.segment.SegmentDestination;
import com.fr.third.fasterxml.jackson.annotation.JsonInclude;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.util.Set;

/**
 * Created by Lyon on 2018/6/3.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractQueryInfoBean implements QueryInfoBean {

    @JsonProperty
    private String queryId;
    @JsonProperty
    protected QueryType queryType;
    @JsonProperty
    protected Set<URI> querySegment;
    @JsonProperty
    protected SegmentDestination queryDestination;

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

    public Set<URI> getQuerySegments() {
        return querySegment;
    }

    @Override
    public void setQuerySegments(Set<URI> querySegment) {
        this.querySegment = querySegment;
    }

    @Override
    public SegmentDestination getQueryDestination() {
        return queryDestination;
    }

    @Override
    public void setQueryDestination(SegmentDestination queryDestination) {
        this.queryDestination = queryDestination;
    }
}
