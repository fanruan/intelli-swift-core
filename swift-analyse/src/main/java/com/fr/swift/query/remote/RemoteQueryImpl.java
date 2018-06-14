package com.fr.swift.query.remote;

import com.fr.swift.query.RemoteQuery;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.query.query.RemoteQueryInfoManager;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.structure.Pair;

import java.sql.SQLException;

/**
 * Created by pony on 2017/11/29.
 */
public class RemoteQueryImpl<T extends SwiftResultSet> implements RemoteQuery<T> {

    private QueryInfo<T> queryInfo;
    private SegmentDestination remoteURI;

    public RemoteQueryImpl(QueryInfo<T> queryInfo, SegmentDestination remoteURI) {
        this.queryInfo = queryInfo;
        this.remoteURI = remoteURI;
    }

    @Override
    public T getQueryResult() throws SQLException {
        RemoteQueryInfoManager.getInstance().put(queryInfo.getQueryId(), Pair.<QueryInfo, SegmentDestination>of(queryInfo, remoteURI));
        return QueryRunnerProvider.getInstance().executeRemoteQuery(queryInfo, remoteURI);
    }
}
