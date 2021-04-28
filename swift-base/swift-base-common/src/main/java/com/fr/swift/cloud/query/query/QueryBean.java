package com.fr.swift.cloud.query.query;

import java.util.Set;

/**
 * @author yee
 * @date 2018/6/26
 */
public interface QueryBean {

    /**
     * 获取查询ID
     *
     * @return
     */
    String getQueryId();

    void setQueryId(String queryId);

    /**
     * 获取查询类型
     * @return
     */
    QueryType getQueryType();

    void setQueryType(QueryType queryType);

    /**
     * 获取查询表名
     * @return
     */
    String getTableName();

    /**
     * 获取要查询的SegmentId
     * @return
     */
    Set<String> getSegments();

    void setSegments(Set<String> uri);
}
