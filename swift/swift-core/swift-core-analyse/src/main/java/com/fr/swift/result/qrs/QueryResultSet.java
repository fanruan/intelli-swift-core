package com.fr.swift.result.qrs;

import com.fr.swift.result.Pagination;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Closable;

/**
 * @author lyon
 * @date 2018/11/21
 */
public interface QueryResultSet<T> extends Pagination<T>, Closable {

    int getFetchSize();

    <Q extends QueryResultSet<T>> QueryResultSetMerger<T, Q> getMerger();

    SwiftResultSet convert(SwiftMetaData metaData);
}
