package com.fr.swift.query.query;

import java.io.Serializable;
import java.net.URI;
import java.util.Set;

/**
 * @author yee
 * @date 2018/6/26
 */
public interface QueryBean extends Serializable {
    String getQueryId();

    QueryType getQueryType();

    void setQueryType(QueryType queryType);

    void setQuerySegment(Set<URI> uri);
}
