package com.fr.swift.query.post;

import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.BaseNodeQRS;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeOperator;
import com.fr.swift.result.node.GroupNodeUtils;
import com.fr.swift.result.node.resultset.ChainedNodeQRS;
import com.fr.swift.result.qrs.QueryResultSet;

/**
 * Created by Lyon on 2018/5/31.
 */
public class UpdateNodeDataQuery implements Query<QueryResultSet<SwiftNode>> {

    private QueryResultSet<GroupPage> resultSet;

    public UpdateNodeDataQuery(QueryResultSet<GroupPage> resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public QueryResultSet<SwiftNode> getQueryResult() {
        return new ChainedNodeQRS(new SwiftNodeOperator() {
            @Override
            public SwiftNode apply(SwiftNode p) {
                return p;
            }
        }, new UpdateDataQRS(resultSet));
    }

    private static class UpdateDataQRS extends BaseNodeQRS {

        private QueryResultSet<GroupPage> resultSet;

        UpdateDataQRS(QueryResultSet<GroupPage> resultSet) {
            super(resultSet.getFetchSize());
            this.resultSet = resultSet;
        }

        @Override
        public SwiftNode getPage() {
            SwiftNode ret = null;
            if (hasNextPage()) {
                GroupPage page = resultSet.getPage();
                ret = page.getRoot();
                GroupNodeUtils.updateNodeData(page.getRoot(), page.getGlobalDicts());
            }
            return ret;
        }

        @Override
        public boolean hasNextPage() {
            return resultSet.hasNextPage();
        }
    }
}
