package com.fr.swift.result.node.resultset;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.result.BaseNodeMergeQRS;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author Lyon
 * @date 2018/6/14
 */
class ChainedNodeMergeQRS extends BaseNodeMergeQRS {

    private Iterator<QueryResultSet<GroupPage>> iterator;
    private QueryResultSetMerger<QueryResultSet<GroupPage>> merger;

    ChainedNodeMergeQRS(int fetchSize, boolean[] isGlobalIndexed, List<QueryResultSet<GroupPage>> sources,
                        List<Aggregator> aggregators, List<Comparator<SwiftNode>> comparators,
                        QueryResultSetMerger<QueryResultSet<GroupPage>> merger) {
        super(fetchSize);
        this.iterator = new NodeResultSetMerger(fetchSize, isGlobalIndexed, sources, aggregators, comparators);
        this.merger = merger;
    }

    @Override
    public GroupPage getPage() {
        if (!iterator.hasNext()) {
            return null;
        }
        QueryResultSet<GroupPage> resultSet = iterator.next();
        return resultSet.getPage();
    }

    @Override
    public boolean hasNextPage() {
        return iterator.hasNext();
    }

    @Override
    public QueryResultSetMerger<QueryResultSet<GroupPage>> getMerger() {
        return merger;
    }
}
