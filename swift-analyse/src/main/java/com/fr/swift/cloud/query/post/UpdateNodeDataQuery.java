package com.fr.swift.cloud.query.post;

import com.fr.swift.cloud.query.group.by2.node.GroupPage;
import com.fr.swift.cloud.query.query.Query;
import com.fr.swift.cloud.result.BaseNodeQueryResultSet;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.result.SwiftNodeOperator;
import com.fr.swift.cloud.result.node.GroupNodeUtils;
import com.fr.swift.cloud.result.node.resultset.ChainedNodeQueryResultSet;
import com.fr.swift.cloud.result.qrs.QueryResultSet;

/**
 * @author Lyon
 * @date 2018/5/31
 */
public class UpdateNodeDataQuery implements Query<QueryResultSet<SwiftNode>> {

    private QueryResultSet<GroupPage> resultSet;

    public UpdateNodeDataQuery(QueryResultSet<GroupPage> resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public QueryResultSet<SwiftNode> getQueryResult() {
        return new ChainedNodeQueryResultSet(new SwiftNodeOperator() {
            @Override
            public SwiftNode apply(SwiftNode p) {
                return p;
            }
        }, new UpdateDataQueryResultSet(resultSet));
    }

    private static class UpdateDataQueryResultSet extends BaseNodeQueryResultSet {

        private QueryResultSet<GroupPage> resultSet;

        UpdateDataQueryResultSet(QueryResultSet<GroupPage> resultSet) {
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
