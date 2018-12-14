package com.fr.swift.result.funnel;

import com.fr.swift.result.FunnelResultSet;
import com.fr.swift.result.qrs.DSType;
import com.fr.swift.result.qrs.QueryResultSet;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelQueryResultSet implements QueryResultSet<FunnelResultSet> {

    private FunnelResultSet funnelResultSet;

    public FunnelQueryResultSet(FunnelResultSet funnelResultSet) {
        this.funnelResultSet = funnelResultSet;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public DSType type() {
        return DSType.ROW;
    }

    @Override
    public FunnelResultSet getPage() {
        return funnelResultSet;
    }

    @Override
    public boolean hasNextPage() {
        return false;
    }
}
