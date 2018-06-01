package com.fr.swift.query.post;

import com.fr.swift.query.info.target.GroupTarget;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.node.cal.TargetCalculatorUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Lyon on 2018/5/31.
 */
public class ResultCalQuery extends AbstractPostQuery<NodeResultSet> {

    private PostQuery<NodeResultSet> query;
    private List<GroupTarget> calInfo;

    public ResultCalQuery(PostQuery<NodeResultSet> query, List<GroupTarget> calInfo) {
        this.query = query;
        this.calInfo = calInfo;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        NodeMergeResultSet<GroupNode> mergeResult = (NodeMergeResultSet<GroupNode>) query.getQueryResult();
        TargetCalculatorUtils.calculate(((GroupNode) mergeResult.getNode()), mergeResult.getRowGlobalDictionaries(), calInfo);
        return mergeResult;
    }
}
