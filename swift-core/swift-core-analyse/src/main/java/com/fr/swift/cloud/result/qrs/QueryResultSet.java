package com.fr.swift.cloud.result.qrs;

import com.fr.swift.cloud.result.Pagination;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.source.SwiftMetaData;

/**
 * @author lyon
 * @date 2018/11/21
 */
public interface QueryResultSet<T> extends Pagination<T> {
    /**
     * 一次取多少条，一页大小
     *
     * @return fetch size
     */
    int getFetchSize();

    /**
     * @param metaData
     * @return
     * @deprecated 独立出去，解耦
     */
    @Deprecated
    SwiftResultSet convert(SwiftMetaData metaData);
}
