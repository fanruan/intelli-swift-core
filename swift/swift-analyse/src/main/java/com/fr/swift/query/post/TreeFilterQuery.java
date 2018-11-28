package com.fr.swift.query.post;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.filter.match.NodeFilter;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.SwiftNodeUtils;
import com.fr.swift.result.node.GroupNodeAggregateUtils;
import com.fr.swift.result.node.resultset.ChainedNodeResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeFilterQuery implements PostQuery<QueryResultSet> {

    private PostQuery<QueryResultSet> query;
    private List<MatchFilter> matchFilterList;
    // TODO: 2018/6/13 这个遍要把用于明细的聚合器传过来
    private List<Aggregator> aggregators;

    public TreeFilterQuery(PostQuery<QueryResultSet> query, List<MatchFilter> matchFilterList) {
        this.query = query;
        this.matchFilterList = matchFilterList;
    }

    @Override
    public QueryResultSet getQueryResult() throws SQLException {
        SwiftNodeOperator operator = new SwiftNodeOperator() {
            @Override
            public Pair<SwiftNode, List<Map<Integer, Object>>> apply(Pair<? extends SwiftNode, List<Map<Integer, Object>>> p) {
                // 先做节点合计，再做过滤
                GroupNodeAggregateUtils.aggregateMetric(SwiftNodeUtils.getDimensionSize(p.getKey()),
                        (GroupNode) p.getKey(), aggregators);
                NodeFilter.filter(p.getKey(), matchFilterList);
                return Pair.of(p.getKey(), p.getValue());
            }
        };
        NodeResultSet<SwiftNode> mergeResult = (NodeResultSet<SwiftNode>) query.getQueryResult();
        // TODO: 2018/11/27
        return (QueryResultSet) new ChainedNodeResultSet(operator, mergeResult);
    }
}
