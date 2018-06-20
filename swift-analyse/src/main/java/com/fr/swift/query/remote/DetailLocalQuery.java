package com.fr.swift.query.remote;

import com.fr.swift.query.LocalQuery;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.query.QueryResultSetManager;
import com.fr.swift.result.DetailResultSet;
import com.fr.swift.result.serialize.SerializableDetailResultSet;

import java.sql.SQLException;

/**
 * Created by Lyon on 2018/6/20.
 */
public class DetailLocalQuery implements LocalQuery<DetailResultSet> {

    private String queryId;
    private Query<DetailResultSet> query;

    public DetailLocalQuery(String queryId, Query<DetailResultSet> query) {
        this.queryId = queryId;
        this.query = query;
    }

    @Override
    public DetailResultSet getQueryResult() throws SQLException {
        // 缓存结果集和取出结果集用于序列化
        DetailResultSet resultSet = query.getQueryResult();
        QueryResultSetManager.getInstance().put(queryId, resultSet);
        return new SerializableDetailResultSet(queryId, resultSet);
    }
}
