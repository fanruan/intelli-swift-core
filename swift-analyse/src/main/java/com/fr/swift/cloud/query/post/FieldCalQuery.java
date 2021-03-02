package com.fr.swift.cloud.query.post;

import com.fr.swift.cloud.query.info.element.target.GroupTarget;
import com.fr.swift.cloud.query.query.Query;
import com.fr.swift.cloud.result.GroupNode;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.result.SwiftNodeOperator;
import com.fr.swift.cloud.result.node.cal.TargetCalculatorUtils;
import com.fr.swift.cloud.result.node.resultset.ChainedNodeQueryResultSet;
import com.fr.swift.cloud.result.qrs.QueryResultSet;
import com.fr.swift.cloud.util.Crasher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Lyon on 2018/5/31.
 */
public class FieldCalQuery implements Query<QueryResultSet<SwiftNode>> {

    private Query<QueryResultSet<SwiftNode>> query;
    private GroupTarget calInfo;

    public FieldCalQuery(Query<QueryResultSet<SwiftNode>> query, GroupTarget calInfo) {
        this.query = query;
        this.calInfo = calInfo;
    }

    @Override
    public QueryResultSet<SwiftNode> getQueryResult() throws SQLException {
        SwiftNodeOperator operator = new SwiftNodeOperator() {
            @Override
            public SwiftNode apply(SwiftNode node) {
                // TODO: 2018/6/13 同比环比依赖的字典去掉了，data已经set进来了，到时适配一下
                try {
                    TargetCalculatorUtils.calculate((GroupNode) node, new ArrayList<Map<Integer, Object>>(), Collections.singletonList(calInfo));
                } catch (SQLException e) {
                    Crasher.crash(e);
                }
                return node;
            }
        };
        return new ChainedNodeQueryResultSet(operator, query.getQueryResult());
    }
}
