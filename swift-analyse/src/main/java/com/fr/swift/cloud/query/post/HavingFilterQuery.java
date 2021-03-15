package com.fr.swift.cloud.query.post;

import com.fr.swift.cloud.query.filter.match.MatchFilter;
import com.fr.swift.cloud.query.filter.match.NodeFilter;
import com.fr.swift.cloud.query.query.Query;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.result.SwiftNodeOperator;
import com.fr.swift.cloud.result.node.resultset.ChainedNodeQueryResultSet;
import com.fr.swift.cloud.result.qrs.QueryResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Lyon on 2018/5/31.
 */
public class HavingFilterQuery implements Query<QueryResultSet<SwiftNode>> {

    private Query<QueryResultSet<SwiftNode>> query;
    private List<MatchFilter> matchFilterList;

    public HavingFilterQuery(Query<QueryResultSet<SwiftNode>> query, List<MatchFilter> matchFilterList) {
        this.query = query;
        this.matchFilterList = matchFilterList;
    }

    @Override
    public QueryResultSet<SwiftNode> getQueryResult() throws SQLException {
        SwiftNodeOperator operator = new SwiftNodeOperator() {
            @Override
            public SwiftNode apply(SwiftNode node) {
                // 没有分组只有聚合的时候的post过滤
                if (node.getChildren().isEmpty()) {
                    for (MatchFilter matchFilter : matchFilterList) {
                        if (!matchFilter.matches(node)) {
                            return null;
                        }
                    }
                    return node;
                }
                NodeFilter.filter(node, matchFilterList);
                return node;
            }
        };
        return new ChainedNodeQueryResultSet(operator, query.getQueryResult());
    }
}
