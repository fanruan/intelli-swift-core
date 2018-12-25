package com.fr.swift.result.node.resultset;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;

import java.util.Comparator;
import java.util.List;

/**
 * @author anchore
 * @date 2018/12/19
 */
public class NodeQueryResultSetMerger implements INodeQueryResultSetMerger<GroupNode> {

    private int fetchSize;

    private boolean[] isGlobalIndexed;

    private List<Aggregator> aggregators;

    private List<Comparator<GroupNode>> comparators;

    public NodeQueryResultSetMerger(int fetchSize, boolean[] isGlobalIndexed, List<Aggregator> aggregators, List<Comparator<GroupNode>> comparators) {
        this.fetchSize = fetchSize;
        this.isGlobalIndexed = isGlobalIndexed;
        this.aggregators = aggregators;
        this.comparators = comparators;
    }

    @Override
    public NodeMergeResultSet<GroupNode> merge(List<NodeMergeResultSet<GroupNode>> resultSets) {
        return new ChainedNodeMergeResultSet(fetchSize, isGlobalIndexed, resultSets, aggregators, comparators, this);
    }
}