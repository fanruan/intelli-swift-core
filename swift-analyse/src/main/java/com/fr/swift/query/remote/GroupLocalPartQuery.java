package com.fr.swift.query.remote;

import com.fr.swift.query.query.Query;
import com.fr.swift.query.query.QueryResultSetManager;
import com.fr.swift.query.result.ResultQuery;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.serialize.LocalPartNodeResultSet;

import java.sql.SQLException;

/**
 * Created by Lyon on 2018/6/14.
 */
public class GroupLocalPartQuery implements ResultQuery<NodeResultSet> {

    private String queryId;
    private Query<NodeResultSet> query;

    public GroupLocalPartQuery(String queryId, Query<NodeResultSet> query) {
        this.queryId = queryId;
        this.query = query;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        // 缓存结果集和取出结果集用于序列化
        NodeMergeResultSet resultSet = (NodeMergeResultSet) query.getQueryResult();
        QueryResultSetManager.getInstance().put(queryId, resultSet);
        return new LocalPartNodeResultSet(queryId, resultSet);
    }
}
