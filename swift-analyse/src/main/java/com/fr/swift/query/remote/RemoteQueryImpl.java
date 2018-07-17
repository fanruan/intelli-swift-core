package com.fr.swift.query.remote;

import com.fr.swift.query.RemoteQuery;
import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * Created by pony on 2017/11/29.
 */
public class RemoteQueryImpl<T extends SwiftResultSet> implements RemoteQuery<T> {

    private String jsonString;
    private SegmentDestination remoteURI;

    public RemoteQueryImpl(String jsonString, SegmentDestination remoteURI) {
        this.jsonString = jsonString;
        this.remoteURI = remoteURI;
    }

    @Override
    public T getQueryResult() throws SQLException {
        return (T) QueryRunnerProvider.getInstance().executeRemoteQuery(jsonString, remoteURI);
    }
}
