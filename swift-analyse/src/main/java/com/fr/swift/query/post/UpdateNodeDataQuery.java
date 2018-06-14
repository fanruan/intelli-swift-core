package com.fr.swift.query.post;

import com.fr.swift.query.result.ResultQuery;
import com.fr.swift.result.ChainedNodeResultSet;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.node.GroupNodeUtils;

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
        // TODO: 2018/6/13 mergeResultSet要调整一下
        // 这边区分分页和不分页吗？分页在MergeResultSet里面处理吗？
        NodeMergeResultSet<GroupNode> mergeResult = (NodeMergeResultSet<GroupNode>) query.getQueryResult();
        final List<Map<Integer, Object>> dictionary = mergeResult.getRowGlobalDictionaries();
        SwiftNodeOperator<SwiftNode> operator = new SwiftNodeOperator<SwiftNode>() {
            @Override
            public SwiftNode operate(SwiftNode... node) {
                GroupNodeUtils.updateNodeData(dictionary.size(), (GroupNode) node[0], dictionary);
                return node[0];
            }
        };
        return new ChainedNodeResultSet(operator, mergeResult);
    }
}
