package com.fr.swift.result;

import com.fr.swift.result.qrs.DSType;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.util.List;

/**
 * @author yee
 * @date 2018-12-13
 */
public abstract class BaseDetailQueryResultSet implements QueryResultSet<List<Row>> {
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
