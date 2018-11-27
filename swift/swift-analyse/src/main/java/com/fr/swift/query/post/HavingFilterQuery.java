package com.fr.swift.query.post;

import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.filter.match.NodeFilter;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.QueryResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.node.resultset.ChainedNodeResultSet;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
            public Pair<SwiftNode, List<Map<Integer, Object>>> apply(Pair<? extends SwiftNode, List<Map<Integer, Object>>> p) {
                NodeFilter.filter(p.getKey(), matchFilterList);
                return Pair.of(p.getKey(), p.getValue());
            }
        };
        NodeResultSet<SwiftNode> mergeResult = (NodeResultSet<SwiftNode>) query.getQueryResult();
        return (QueryResultSet) new ChainedNodeResultSet(operator, mergeResult);
    }
}
