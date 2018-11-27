package com.fr.swift.query.post;

import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.QueryResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.node.cal.TargetCalculatorUtils;
import com.fr.swift.result.node.resultset.ChainedNodeResultSet;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.Crasher;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/5/31.
 */
public class FieldCalQuery implements PostQuery<QueryResultSet> {

    private PostQuery<QueryResultSet> query;
    private List<GroupTarget> calInfo;

    public FieldCalQuery(PostQuery<QueryResultSet> query, List<GroupTarget> calInfo) {
        this.query = query;
        this.calInfo = calInfo;
    }

    @Override
    public QueryResultSet getQueryResult() throws SQLException {
        SwiftNodeOperator operator = new SwiftNodeOperator() {
            @Override
            public Pair<SwiftNode, List<Map<Integer, Object>>> apply(Pair<? extends SwiftNode, List<Map<Integer, Object>>> p) {
                // TODO: 2018/6/13 同比环比依赖的字典去掉了，data已经set进来了，到时适配一下
                try {
                    TargetCalculatorUtils.calculate((GroupNode) p.getKey(), p.getValue(), calInfo);
                } catch (SQLException e) {
                    Crasher.crash(e);
                }
                return Pair.of(p.getKey(), p.getValue());
            }
        };
        NodeResultSet<SwiftNode> mergeResult = (NodeResultSet<SwiftNode>) query.getQueryResult();
        // TODO: 2018/11/27
        return (QueryResultSet) new ChainedNodeResultSet(operator, mergeResult);
    }
}
