package com.fr.swift.query.info;

import com.fr.swift.query.builder.QueryType;

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
}
