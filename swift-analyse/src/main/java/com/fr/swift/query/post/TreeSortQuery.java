package com.fr.swift.query.post;

import com.fr.swift.query.filter.match.NodeSorter;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.node.resultset.ChainedNodeQRS;
import com.fr.swift.result.qrs.QueryResultSet;

import java.sql.SQLException;
import java.util.List;

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
            public SwiftNode apply(SwiftNode node) {
                NodeSorter.sort(node, sortList);
                return node;
            }
        };
        return new ChainedNodeQRS(operator, query.getQueryResult());
    }
}
