package com.fr.swift.result.qrs;

import com.fr.swift.result.Pagination;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.SwiftMetaData;

/**
 * @author lyon
 * @date 2018/11/21
 */
public interface QueryResultSet<T> extends Pagination<T> {

    int getFetchSize();

    /**
     * @return
     * @deprecated 独立出去，解耦
     */
    @Deprecated
    <Q extends QueryResultSet<T>> QueryResultSetMerger<Q> getMerger();

    /**
     * @param metaData
     * @return
     * @deprecated 独立出去，解耦
     */
    @Deprecated
    SwiftResultSet convert(SwiftMetaData metaData);
}
