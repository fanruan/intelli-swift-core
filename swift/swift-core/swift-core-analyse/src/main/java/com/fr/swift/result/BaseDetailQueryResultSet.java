package com.fr.swift.result;

import com.fr.swift.result.qrs.DSType;
import com.fr.swift.source.SwiftMetaData;

/**
 * @author yee
 * @date 2018-12-13
 */
public abstract class BaseDetailQueryResultSet implements DetailQueryResultSet {
    protected int fetchSize;
    protected SwiftMetaData metaData;

    public BaseDetailQueryResultSet(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public void setMetaData(SwiftMetaData metaData) {
        this.metaData = metaData;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public DSType type() {
        return DSType.ROW;
    }
}
