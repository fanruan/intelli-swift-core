package com.fr.swift.result;

import com.fr.swift.result.qrs.QueryResultSetMerger;

import java.io.Serializable;
import java.util.List;

/**
 * @author yee
 * @date 2018-12-16
 */
public class DetailQueryResultSetMerger implements QueryResultSetMerger<DetailQueryResultSet>, Serializable {

    private static final long serialVersionUID = -324445209089300987L;
    private int fetchSize;

    public DetailQueryResultSetMerger(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    @Override
    public DetailQueryResultSet merge(List<DetailQueryResultSet> queryResultSets) {
        return new MergeDetailQueryResultSet(fetchSize, queryResultSets);
    }

}
