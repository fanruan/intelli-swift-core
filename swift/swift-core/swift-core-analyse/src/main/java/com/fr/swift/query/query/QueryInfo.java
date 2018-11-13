package com.fr.swift.query.query;

import java.util.Set;

/**
 * Created by pony on 2017/12/12.
 */
public interface QueryInfo<T> {

    /**
     * 查询id
     *
     * @return
     */
    String getQueryId();

    /**
     * 查询类型
     *
     * @return
     */
    QueryType getType();

    int getFetchSize();

    /**
     * 还是把某个SegmentURI放到queryinfo里面
     * 不然不好判断查单个Segment还是所有Segment
     *
     * @return
     */
    Set<String> getQuerySegment();

    void setQuerySegment(Set<String> target);
}
