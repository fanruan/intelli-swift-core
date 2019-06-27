package com.fr.swift.result;

import com.fr.swift.source.SwiftMetaData;

/**
 * @author lyon
 * @date 2018/12/29
 */
public abstract class BaseNodeQRS implements NodeQRS {

    private int fetchSize;

    public BaseNodeQRS(int fetchSize) {
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
