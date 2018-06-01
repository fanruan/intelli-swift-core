package com.fr.swift.query.remote;

import com.fr.swift.query.QueryInfo;
import com.fr.swift.query.QueryRunnerProvider;
import com.fr.swift.query.RemoteQuery;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * Created by pony on 2017/11/29.
 */
public class RemoteQueryImpl<T extends SwiftResultSet> implements RemoteQuery<T> {

    private QueryInfo<T> queryInfo;

    public RemoteQueryImpl(QueryInfo<T> queryInfo) {
        this.queryInfo = queryInfo;
    }

    @Override
    public T getQueryResult() throws SQLException {
        return QueryRunnerProvider.getInstance().executeRemoteQuery(queryInfo);
    }
}
