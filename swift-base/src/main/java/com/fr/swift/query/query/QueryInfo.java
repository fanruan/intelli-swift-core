package com.fr.swift.query.query;

import java.net.URI;

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

    /**
     * 还是把某个SegmentURI放到queryinfo里面
     * 不然不好判断查单个Segment还是所有Segment
     *
     * @return
     */
    URI getQuerySegment();

    void setQuerySegment(URI target);
}
