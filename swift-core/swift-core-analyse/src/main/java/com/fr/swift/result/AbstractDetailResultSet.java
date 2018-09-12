package com.fr.swift.result;

import com.fr.swift.source.SwiftMetaData;

/**
 * Created by Lyon on 2018/7/25.
 */
public abstract class AbstractDetailResultSet implements DetailResultSet {

    protected int fetchSize;
    protected SwiftMetaData metaData;

    public AbstractDetailResultSet(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    @Override
    public void setMetaData(SwiftMetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }
}
