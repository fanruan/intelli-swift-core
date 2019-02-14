package com.fr.swift.query.result.group;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.AbstractResultQuery;
import com.fr.swift.result.node.resultset.INodeQueryResultSetMerger;
import com.fr.swift.result.qrs.QueryResultSet;

import java.util.List;

/**
 * Created by pony on 2017/12/18.
 */
public abstract class AbstractGroupResultQuery extends AbstractResultQuery<QueryResultSet> {

    protected List<Aggregator> aggregators;

    public AbstractGroupResultQuery(int fetchSize, List<Query<QueryResultSet>> queries, List<Aggregator> aggregators) {
        super(fetchSize, queries);
        this.aggregators = aggregators;
    }

    protected abstract INodeQueryResultSetMerger createMerger();
}
