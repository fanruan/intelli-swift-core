package com.fr.swift.result;

/**
 * Created by lyon on 2018/11/21.
 */
public interface QueryResultSet<T> {

    int getFetchSize();

    /**
     * 获取一页数据
     *
     * @return
     */
    T getPage();

    /**
     * 是否有下一页
     *
     * @return
     */
    boolean hasNextPage();
}
