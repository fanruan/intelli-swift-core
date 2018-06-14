package com.fr.swift.query.post;

import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.filter.match.NodeFilter;
import com.fr.swift.result.ChainedNodeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Lyon on 2018/5/31.
 */
public class HavingFilterQuery extends AbstractPostQuery<NodeResultSet> {

    private PostQuery<NodeResultSet> query;
    private List<MatchFilter> matchFilterList;

    public HavingFilterQuery(PostQuery<NodeResultSet> query, List<MatchFilter> matchFilterList) {
        this.query = query;
        this.matchFilterList = matchFilterList;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        NodeResultSet<SwiftNode> mergeResult = (NodeResultSet<SwiftNode>) query.getQueryResult();
        SwiftNodeOperator<SwiftNode> operator = new SwiftNodeOperator<SwiftNode>() {
            @Override
            public SwiftNode operate(SwiftNode... node) {
                NodeFilter.filter(node[0], matchFilterList);
                return node[0];
            }
        };
        return new ChainedNodeResultSet(operator, mergeResult);
    }
}
