package com.fr.swift.result.node.resultset;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.result.BaseNodeMergeQRS;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeQRS;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.structure.Pair;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Lyon
 * @date 2018/6/14
 */
class ChainedNodeMergeQRS extends BaseNodeMergeQRS<GroupNode> {

    private Iterator<NodeMergeQRS<GroupNode>> iterator;
    private INodeQueryResultSetMerger merger;

    ChainedNodeMergeQRS(int fetchSize, boolean[] isGlobalIndexed, List<NodeMergeQRS<GroupNode>> sources,
                        List<Aggregator> aggregators, List<Comparator<GroupNode>> comparators,
                        INodeQueryResultSetMerger merger) {
        super(fetchSize);
        this.iterator = new NodeResultSetMerger(fetchSize, isGlobalIndexed, sources, aggregators, comparators);
        this.merger = merger;
    }

    @Override
    public Pair<GroupNode, List<Map<Integer, Object>>> getPage() {
        if (!iterator.hasNext()) {
            return null;
        }
        NodeMergeQRS<GroupNode> resultSet = iterator.next();
        return resultSet.getPage();
    }

    @Override
    public boolean hasNextPage() {
        return iterator.hasNext();
    }

    @Override
    public <Q extends QueryResultSet<Pair<GroupNode, List<Map<Integer, Object>>>>> QueryResultSetMerger<Pair<GroupNode, List<Map<Integer, Object>>>, Q> getMerger() {
        return merger;
    }
}
