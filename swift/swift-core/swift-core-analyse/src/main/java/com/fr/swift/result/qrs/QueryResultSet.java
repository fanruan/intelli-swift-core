package com.fr.swift.result.qrs;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.SwiftMetaData;

/**
 * @author lyon
 * @date 2018/11/21
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

    <Q extends QueryResultSet<T>> QueryResultSetMerger<T, Q> getMerger();

    SwiftResultSet convert(SwiftMetaData metaData);
}
