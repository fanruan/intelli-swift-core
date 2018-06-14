package com.fr.swift.query.query;

import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * Created by pony on 2017/12/20.
 */
public class QueryRunnerProvider {
    private static QueryRunnerProvider ourInstance = new QueryRunnerProvider();

    public static QueryRunnerProvider getInstance() {
        return ourInstance;
    }

    private QueryRunner runner;

    private QueryRunnerProvider() {
    }

    public void registerRunner(QueryRunner runner) {
        this.runner = runner;
    }

    public <T extends SwiftResultSet> T executeQuery(QueryInfo<T> info) throws SQLException {
        return runner.getQueryResult(info);
    }

    public <T extends SwiftResultSet> T executeRemoteQuery(QueryInfo<T> info, SegmentDestination remoteURI) throws SQLException {
        return runner.getRemoteQueryResult(info, remoteURI);
    }
}
