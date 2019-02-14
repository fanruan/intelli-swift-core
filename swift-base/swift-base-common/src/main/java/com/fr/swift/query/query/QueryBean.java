package com.fr.swift.query.query;

import java.util.Set;

/**
 * @author yee
 * @date 2018/6/26
 */
public interface QueryBean {

    String getQueryId();

    void setQueryId(String queryId);

    QueryType getQueryType();

    void setQueryType(QueryType queryType);

    String getTableName();

    Set<String> getSegments();

    void setSegments(Set<String> uri);
}
