package com.fr.swift.query.post;

import com.fr.swift.query.filter.match.NodeSorter;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.node.resultset.ChainedNodeResultSet;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/6/3.
 */
public class TreeSortQuery extends AbstractPostQuery<NodeResultSet> {

    private PostQuery<NodeResultSet> query;
    private List<Sort> sortList;

    public TreeSortQuery(PostQuery<NodeResultSet> query, List<Sort> sortList) {
        this.query = query;
        this.sortList = sortList;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        SwiftNodeOperator operator = new SwiftNodeOperator() {
            @Override
            public Pair<SwiftNode, List<Map<Integer, Object>>> apply(Pair<? extends SwiftNode, List<Map<Integer, Object>>> p) {
                NodeSorter.sort(p.getKey(), sortList);
                return Pair.of(p.getKey(), p.getValue());
            }
        };
        NodeResultSet<SwiftNode> mergeResult = (NodeResultSet<SwiftNode>) query.getQueryResult();
        return new ChainedNodeResultSet(operator, mergeResult);
    }
}
