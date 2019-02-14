package com.fr.swift.result;

import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.util.List;

/**
 * @author yee
 * @date 2018-12-13
 */
public abstract class BaseDetailQueryResultSet implements DetailQueryResultSet {
    protected int fetchSize;

    public BaseDetailQueryResultSet(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    @Override
    public <Q extends QueryResultSet<List<Row>>> QueryResultSetMerger<List<Row>, Q> getMerger() {
        throw new UnsupportedOperationException();
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
