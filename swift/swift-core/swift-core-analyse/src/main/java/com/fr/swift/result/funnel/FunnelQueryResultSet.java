package com.fr.swift.result.funnel;

import com.fr.swift.result.FunnelResultSet;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.source.SwiftMetaData;

import java.io.Serializable;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelQueryResultSet implements QueryResultSet<FunnelResultSet>, Serializable {

    private static final long serialVersionUID = 5730624500314766659L;

    private FunnelResultSet funnelResultSet;
    private QueryResultSetMerger merger;

    public FunnelQueryResultSet(FunnelResultSet funnelResultSet, QueryResultSetMerger merger) {
        this.funnelResultSet = funnelResultSet;
        this.merger = merger;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public FunnelResultSet getPage() {
        return funnelResultSet;
    }

    @Override
    public boolean hasNextPage() {
        return false;
    }

    @Override
    public <Q extends QueryResultSet<FunnelResultSet>> QueryResultSetMerger<FunnelResultSet, Q> getMerger() {
        return merger;
    }

    @Override
    public SwiftResultSet convert(SwiftMetaData metaData) {
        throw new UnsupportedOperationException();
    }
}
