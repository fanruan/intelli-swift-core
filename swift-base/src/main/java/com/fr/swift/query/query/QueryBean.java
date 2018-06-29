package com.fr.swift.query.query;

import java.io.Serializable;
import java.net.URI;

/**
 * @author yee
 * @date 2018/6/26
 */
public interface QueryBean extends Serializable {
    String getQueryId();

    QueryType getQueryType();

    void setQueryType(QueryType queryType);

    void setQuerySegment(URI uri);
}
