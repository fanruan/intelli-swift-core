package com.fr.swift.query.post;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.filter.match.NodeFilter;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.SwiftNodeUtils;
import com.fr.swift.result.node.GroupNodeAggregateUtils;
import com.fr.swift.result.node.resultset.ChainedNodeQRS;
import com.fr.swift.result.qrs.QueryResultSet;

import java.sql.SQLException;
import java.util.List;

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
            public SwiftNode apply(SwiftNode node) {
                // 先做节点合计，再做过滤
                GroupNodeAggregateUtils.aggregateMetric(SwiftNodeUtils.getDimensionSize(node),
                        (GroupNode) node, aggregators);
                NodeFilter.filter(node, matchFilterList);
                return node;
            }
        };
        return new ChainedNodeQRS(operator, query.getQueryResult());
    }
}
