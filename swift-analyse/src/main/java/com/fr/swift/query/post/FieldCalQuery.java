package com.fr.swift.query.post;

import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.result.ChainedNodeResultSet;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.node.cal.TargetCalculatorUtils;
import com.fr.swift.util.Crasher;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Lyon on 2018/5/31.
 */
public class FieldCalQuery extends AbstractPostQuery<NodeResultSet> {

    private PostQuery<NodeResultSet> query;
    private List<GroupTarget> calInfo;

    public FieldCalQuery(PostQuery<NodeResultSet> query, List<GroupTarget> calInfo) {
        this.query = query;
        this.calInfo = calInfo;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        NodeResultSet<GroupNode> mergeResult = (NodeResultSet<GroupNode>) query.getQueryResult();
        SwiftNodeOperator<SwiftNode> operator = new SwiftNodeOperator<SwiftNode>() {
            @Override
            public SwiftNode operate(SwiftNode... node) {
                // TODO: 2018/6/13 同比环比依赖的字典去掉了，data已经set进来了，到时适配一下
                try {
                    TargetCalculatorUtils.calculate((GroupNode) node[0], null, calInfo);
                } catch (SQLException e) {
                    Crasher.crash(e);
                }
                return node[0];
            }
        };
        return new ChainedNodeResultSet(operator, mergeResult);
    }
}
