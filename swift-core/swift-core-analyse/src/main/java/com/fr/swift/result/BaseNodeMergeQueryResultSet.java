package com.fr.swift.result;

import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.SwiftMetaData;

/**
 * @author anchore
 * @date 12/11/2018
 */
public abstract class BaseNodeMergeQueryResultSet implements QueryResultSet<GroupPage> {

    private int fetchSize;

    public BaseNodeMergeQueryResultSet(int fetchSize) {
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
        // todo
    }
}