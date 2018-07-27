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

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeFilterQuery extends AbstractPostQuery<NodeResultSet> {

    private PostQuery<NodeResultSet> query;
    private List<MatchFilter> matchFilterList;
    // TODO: 2018/6/13 这个遍要把用于明细的聚合器传过来
    private List<Aggregator> aggregators;

    public TreeFilterQuery(PostQuery<NodeResultSet> query, List<MatchFilter> matchFilterList) {
        this.query = query;
        this.matchFilterList = matchFilterList;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        NodeResultSet<SwiftNode> mergeResult = (NodeResultSet<SwiftNode>) query.getQueryResult();
        SwiftNodeOperator<SwiftNode> operator = new SwiftNodeOperator<SwiftNode>() {
            @Override
            public SwiftNode operate(SwiftNode... node) {
                // 先做节点合计，再做过滤
                GroupNodeAggregateUtils.aggregateMetric(SwiftNodeUtils.getDimensionSize(node[0]),
                        (GroupNode) node[0], aggregators);
                NodeFilter.filter(node[0], matchFilterList);
                return node[0];
            }
        };
        return new ChainedNodeResultSet(operator, mergeResult);
    }
}
