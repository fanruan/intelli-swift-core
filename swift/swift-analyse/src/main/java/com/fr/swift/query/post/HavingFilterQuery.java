package com.fr.swift.query.post;

import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.filter.match.NodeFilter;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.node.resultset.ChainedNodeQRS;
import com.fr.swift.result.qrs.QueryResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Lyon on 2018/5/31.
 */
public class HavingFilterQuery implements PostQuery<QueryResultSet> {

    private PostQuery<QueryResultSet> query;
    private List<MatchFilter> matchFilterList;

    public HavingFilterQuery(PostQuery<QueryResultSet> query, List<MatchFilter> matchFilterList) {
        this.query = query;
        this.matchFilterList = matchFilterList;
    }

    @Override
    public QueryResultSet getQueryResult() throws SQLException {
        SwiftNodeOperator operator = new SwiftNodeOperator() {
            @Override
            public SwiftNode apply(SwiftNode node) {
                NodeFilter.filter(node, matchFilterList);
                return node;
            }
        };
        return new ChainedNodeQRS(operator, query.getQueryResult());
    }
}
