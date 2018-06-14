package com.fr.swift.query.remote;

import com.fr.swift.query.post.AbstractPostQuery;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.query.QueryResultSetManager;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.serialize.LocalAllNodeResultSet;

import java.sql.SQLException;

/**
 * Created by Lyon on 2018/6/14.
 */
public class GroupLocalAllQuery extends AbstractPostQuery<NodeResultSet> {

    private String queryId;
    private Query<NodeResultSet> query;

    public GroupLocalAllQuery(String queryId, Query<NodeResultSet> query) {
        this.queryId = queryId;
        this.query = query;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        // 缓存结果集和取出结果集用于序列化
        NodeResultSet resultSet = query.getQueryResult();
        QueryResultSetManager.getInstance().put(queryId, resultSet);
        return new LocalAllNodeResultSet(queryId, resultSet);
    }
}
