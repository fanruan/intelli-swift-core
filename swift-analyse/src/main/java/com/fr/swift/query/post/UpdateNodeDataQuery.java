package com.fr.swift.query.post;

import com.fr.swift.query.result.ResultQuery;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.node.GroupNodeUtils;
import com.fr.swift.result.node.resultset.ChainedNodeResultSet;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/5/31.
 */
public class UpdateNodeDataQuery extends AbstractPostQuery<NodeResultSet> {

    private ResultQuery<NodeResultSet> query;

    public UpdateNodeDataQuery(ResultQuery<NodeResultSet> query) {
        this.query = query;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        final NodeMergeResultSet mergeResult = (NodeMergeResultSet) query.getQueryResult();
        final Pair<GroupNode, List<Map<Integer, Object>>> pair = mergeResult.getPage();
        SwiftNodeOperator<SwiftNode> operator = new SwiftNodeOperator<SwiftNode>() {
            @Override
            public SwiftNode operate(SwiftNode... node) {
                GroupNodeUtils.updateNodeData((GroupNode) node[0], pair.getValue());
                return node[0];
            }
        };
        return new ChainedNodeResultSet(operator, mergeResult);
    }
}
