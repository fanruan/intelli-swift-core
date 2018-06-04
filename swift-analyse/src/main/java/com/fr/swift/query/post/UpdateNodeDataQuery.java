package com.fr.swift.query.post;

import com.fr.swift.query.result.ResultQuery;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;
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
        NodeMergeResultSet<GroupNode> mergeResult = (NodeMergeResultSet<GroupNode>) query.getQueryResult();
        List<Map<Integer, Object>> dictionary = mergeResult.getRowGlobalDictionaries();
        GroupNodeUtils.updateNodeData(dictionary.size(), ((GroupNode) mergeResult.getNode()), dictionary);
        // TODO: 2018/6/1 这个可以看情况把NodeMergeResultSet里面的字典扔掉了
        return mergeResult;
    }
}
