package com.fr.swift.query.post;

import com.fr.swift.result.BaseNodeQRS;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.node.GroupNodeUtils;
import com.fr.swift.result.node.resultset.ChainedNodeQRS;
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
        return new ChainedNodeQRS(new SwiftNodeOperator() {
            @Override
            public SwiftNode apply(SwiftNode p) {
                return p;
            }
        }, new UpdateDataQRS(resultSet));
    }

    private static class UpdateDataQRS extends BaseNodeQRS {

        private QueryResultSet<Pair<SwiftNode, List<Map<Integer, Object>>>> resultSet;

        public UpdateDataQRS(QueryResultSet<Pair<SwiftNode, List<Map<Integer, Object>>>> resultSet) {
            super(resultSet.getFetchSize());
            this.resultSet = resultSet;
        }

        @Override
        public SwiftNode getPage() {
            SwiftNode ret = null;
            if (hasNextPage()) {
                Pair<? extends SwiftNode, List<Map<Integer, Object>>> page = resultSet.getPage();
                ret = page.getKey();
                GroupNodeUtils.updateNodeData((GroupNode) page.getKey(), page.getValue());
            }
            return ret;
        }

        @Override
        public boolean hasNextPage() {
            return resultSet.hasNextPage();
        }
    }
}
