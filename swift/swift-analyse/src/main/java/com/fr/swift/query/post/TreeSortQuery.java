package com.fr.swift.query.post;

import com.fr.swift.query.filter.match.NodeSorter;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.node.resultset.ChainedNodeResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeSortQuery implements PostQuery<QueryResultSet> {

    private PostQuery<QueryResultSet> query;
    private List<Sort> sortList;

    public TreeSortQuery(PostQuery<QueryResultSet> query, List<Sort> sortList) {
        this.query = query;
        this.sortList = sortList;
    }

    @Override
    public QueryResultSet getQueryResult() throws SQLException {
        SwiftNodeOperator operator = new SwiftNodeOperator() {
            @Override
            public Pair<SwiftNode, List<Map<Integer, Object>>> apply(Pair<? extends SwiftNode, List<Map<Integer, Object>>> p) {
                NodeSorter.sort(p.getKey(), sortList);
                return Pair.of(p.getKey(), p.getValue());
            }
        };
        NodeResultSet<SwiftNode> mergeResult = (NodeResultSet<SwiftNode>) query.getQueryResult();
        return (QueryResultSet) new ChainedNodeResultSet(operator, mergeResult);
    }
}
