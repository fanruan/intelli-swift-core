package com.fr.swift.query.result.serialize;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.source.SwiftMetaData;

import java.io.Serializable;

/**
 * Created by lyon on 2018/12/29.
 */
abstract class BaseSerializableQRS<T> implements QueryResultSet<T>, Serializable {
    private static final long serialVersionUID = 3284100787389755050L;

    private int fetchSize;
    private QueryResultSetMerger merger;
    protected T page;
    protected boolean originHasNextPage;

    public BaseSerializableQRS(int fetchSize, QueryResultSetMerger merger, boolean originHasNextPage) {
        this.fetchSize = fetchSize;
        this.merger = merger;
        this.originHasNextPage = originHasNextPage;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public <Q extends QueryResultSet<T>> QueryResultSetMerger<T, Q> getMerger() {
        return merger;
    }

    @Override
    public T getPage() {
        T ret = page;
        page = null;
        return ret;
    }

    @Override
    public SwiftResultSet convert(SwiftMetaData metaData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {

    }

    @Override
    public boolean hasNextPage() {
        return page != null || originHasNextPage;
    }
}
