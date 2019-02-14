package com.fr.swift.query.post;

import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.node.cal.TargetCalculatorUtils;
import com.fr.swift.result.node.resultset.ChainedNodeQRS;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.util.Crasher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Lyon on 2018/5/31.
 */
public class FieldCalQuery implements PostQuery<QueryResultSet> {

    private PostQuery<QueryResultSet> query;
    private GroupTarget calInfo;

    public FieldCalQuery(PostQuery<QueryResultSet> query, GroupTarget calInfo) {
        this.query = query;
        this.calInfo = calInfo;
    }

    @Override
    public QueryResultSet getQueryResult() throws SQLException {
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
        return new ChainedNodeQRS(operator, query.getQueryResult());
    }
}
