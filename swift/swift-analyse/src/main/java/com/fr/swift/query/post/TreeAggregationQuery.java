package com.fr.swift.query.post;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeQRS;
import com.fr.swift.result.node.GroupNodeAggregateUtils;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeAggregationQuery implements PostQuery<QueryResultSet> {

    private PostQuery<QueryResultSet> query;
    private List<Aggregator> aggregators;

    public TreeAggregationQuery(PostQuery<QueryResultSet> query, List<Aggregator> aggregators) {
        this.query = query;
        this.aggregators = aggregators;
    }

    @Override
    public QueryResultSet getQueryResult() throws SQLException {
        NodeMergeQRS<GroupNode> mergeResult = (NodeMergeQRS<GroupNode>) query.getQueryResult();
        Pair<GroupNode, List<Map<Integer, Object>>> pair = mergeResult.getPage();
        GroupNodeAggregateUtils.aggregateMetric(pair.getValue().size(), pair.getKey(), aggregators);
        // TODO: 2018/11/27
        return (QueryResultSet) mergeResult;
    }
}
