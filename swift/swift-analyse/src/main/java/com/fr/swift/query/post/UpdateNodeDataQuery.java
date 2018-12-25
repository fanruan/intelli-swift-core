package com.fr.swift.query.post;

import com.fr.swift.result.GroupNode;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.node.GroupNodeUtils;
import com.fr.swift.result.node.resultset.ChainedNodeResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/5/31.
 */
public class UpdateNodeDataQuery implements PostQuery<QueryResultSet> {

    private QueryResultSet<Pair<SwiftNode, List<Map<Integer, Object>>>> resultSet;

    public UpdateNodeDataQuery(QueryResultSet<Pair<SwiftNode, List<Map<Integer, Object>>>> resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public QueryResultSet getQueryResult() throws SQLException {
        SwiftNodeOperator operator = new SwiftNodeOperator() {
            @Override
            public Pair<SwiftNode, List<Map<Integer, Object>>> apply(Pair<? extends SwiftNode, List<Map<Integer, Object>>> p) {
                GroupNodeUtils.updateNodeData((GroupNode) p.getKey(), p.getValue());
                return Pair.of(p.getKey(), null);
            }
        };
        return new ChainedNodeResultSet(operator, resultSet);
    }
}
