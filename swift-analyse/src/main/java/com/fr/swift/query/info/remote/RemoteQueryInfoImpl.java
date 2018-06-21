package com.fr.swift.query.info.remote;

import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.source.SwiftResultSet;

import java.net.URI;

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

    @Override
    public URI getQuerySegment() {
        return queryInfo.getQuerySegment();
    }

    @Override
    public void setQuerySegment(URI target) {
        queryInfo.setQuerySegment(target);
    }

}
