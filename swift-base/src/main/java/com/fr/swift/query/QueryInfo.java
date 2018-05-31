package com.fr.swift.query;

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
