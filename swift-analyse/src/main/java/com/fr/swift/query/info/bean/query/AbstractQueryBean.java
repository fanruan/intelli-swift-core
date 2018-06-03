package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.QueryType;
import com.fr.third.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Lyon on 2018/6/3.
 */
public abstract class AbstractQueryBean {

    @JsonProperty
    private String queryId;
    @JsonProperty
    protected QueryType type;

    public abstract QueryType getType();

    public void setType(QueryType type) {
        this.type = type;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }
}
