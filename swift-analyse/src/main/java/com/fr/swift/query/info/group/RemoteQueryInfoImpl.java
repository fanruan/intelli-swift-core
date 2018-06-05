package com.fr.swift.query.info.group;

import com.fr.swift.query.QueryInfo;
import com.fr.swift.query.QueryType;
import com.fr.swift.source.SwiftResultSet;

/**
 * Created by Lyon on 2018/6/5.
 */
public class RemoteQueryInfoImpl<T extends SwiftResultSet> implements RemoteQueryInfo<T> {

    private QueryType remoteQueryType;
    private QueryInfo<T> queryInfo;

    public RemoteQueryInfoImpl(QueryType remoteQueryType, QueryInfo<T> queryInfo) {
        this.remoteQueryType = remoteQueryType;
        this.queryInfo = queryInfo;
    }

    @Override
    public QueryInfo<T> getQueryInfo() {
        return queryInfo;
    }

    @Override
    public String getQueryId() {
        return queryInfo.getQueryId();
    }

    @Override
    public QueryType getType() {
        return remoteQueryType;
    }
}
