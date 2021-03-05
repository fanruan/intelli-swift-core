package com.fr.swift.cloud.result;

import com.fr.swift.cloud.source.SwiftMetaData;

/**
 * @author lyon
 * @date 2018/12/29
 */
public abstract class BaseNodeQueryResultSet implements NodeQueryResultSet {

    private int fetchSize;

    protected BaseNodeQueryResultSet(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public SwiftResultSet convert(SwiftMetaData metaData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {

    }
}
