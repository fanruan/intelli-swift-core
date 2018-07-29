package com.fr.swift.query.post;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.node.GroupNodeAggregateUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeAggregationQuery extends AbstractPostQuery<NodeResultSet> {

    private PostQuery<NodeResultSet> query;
    private List<Aggregator> aggregators;

    public TreeAggregationQuery(PostQuery<NodeResultSet> query, List<Aggregator> aggregators) {
        this.query = query;
        this.aggregators = aggregators;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        NodeMergeResultSet<GroupNode> mergeResult = (NodeMergeResultSet<GroupNode>) query.getQueryResult();
        GroupNodeAggregateUtils.aggregateMetric(mergeResult.getRowGlobalDictionaries().size(),
                (GroupNode) mergeResult.getNode(), aggregators);
        return mergeResult;
    }
}
