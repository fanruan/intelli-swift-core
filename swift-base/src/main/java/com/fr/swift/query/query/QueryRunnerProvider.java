package com.fr.swift.query.query;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.source.SwiftResultSet;

import java.net.URI;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by pony on 2017/12/20.
 */
public class QueryRunnerProvider {
    private static QueryRunnerProvider ourInstance = new QueryRunnerProvider();

    public static QueryRunnerProvider getInstance() {
        return ourInstance;
    }

    private QueryRunner runner;

    private QueryIndexRunner indexRunner;

    private QueryRunnerProvider() {
    }

    public void registerRunner(QueryRunner runner, QueryIndexRunner indexRunner) {
        this.runner = runner;
        this.indexRunner = indexRunner;
    }

    public SwiftResultSet executeQuery(QueryBean info) throws SQLException {
        return runner.getQueryResult(info);
    }

    public SwiftResultSet executeRemoteQuery(QueryBean info, SegmentDestination remoteURI) throws SQLException {
        return runner.getRemoteQueryResult(info, remoteURI);
    }

    public Map<URI, IndexQuery<ImmutableBitMap>> executeIndexQuery(Table table, Where where) throws Exception {
        return indexRunner.getBitMap(table, where);
    }

    public IndexQuery<ImmutableBitMap> executeIndexQuery(Table table, Where where, Segment segment) throws Exception {
        return indexRunner.getBitMap(table, where, segment);
    }
}
