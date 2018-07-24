package com.fr.swift.query.post;

import com.fr.swift.query.result.ResultQuery;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.node.ChainedNodeResultSet;
import com.fr.swift.result.node.GroupNodeUtils;

import java.sql.SQLException;

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
        final NodeMergeResultSet<GroupNode> mergeResult = (NodeMergeResultSet<GroupNode>) query.getQueryResult();
        SwiftNodeOperator<SwiftNode> operator = new SwiftNodeOperator<SwiftNode>() {
            @Override
            public SwiftNode operate(SwiftNode... node) {
                GroupNodeUtils.updateNodeData(mergeResult.getRowGlobalDictionaries().size(), (GroupNode) node[0], mergeResult.getRowGlobalDictionaries());
                return node[0];
            }
        };
        return new ChainedNodeResultSet(operator, mergeResult);
    }
}
