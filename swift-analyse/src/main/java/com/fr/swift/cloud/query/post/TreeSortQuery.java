package com.fr.swift.cloud.query.post;

import com.fr.swift.cloud.query.filter.match.NodeSorter;
import com.fr.swift.cloud.query.query.Query;
import com.fr.swift.cloud.query.sort.Sort;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.result.SwiftNodeOperator;
import com.fr.swift.cloud.result.node.resultset.ChainedNodeQueryResultSet;
import com.fr.swift.cloud.result.qrs.QueryResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeSortQuery implements Query<QueryResultSet<SwiftNode>> {

    private Query<QueryResultSet<SwiftNode>> query;
    private List<Sort> sortList;

    public TreeSortQuery(Query<QueryResultSet<SwiftNode>> query, List<Sort> sortList) {
        this.query = query;
        this.sortList = sortList;
    }

    @Override
    public QueryResultSet<SwiftNode> getQueryResult() throws SQLException {
        SwiftNodeOperator operator = new SwiftNodeOperator() {
            @Override
            public SwiftNode apply(SwiftNode node) {
                NodeSorter.sort(node, sortList);
                return node;
            }
        };
        return new ChainedNodeQueryResultSet(operator, query.getQueryResult());
    }
}
