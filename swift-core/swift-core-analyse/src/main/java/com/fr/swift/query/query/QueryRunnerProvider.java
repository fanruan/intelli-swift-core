package com.fr.swift.query.query;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.service.cluster.ClusterAnalyseService;
import com.fr.swift.source.SwiftResultSet;

import java.net.URI;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author pony
 * @date 2017/12/20
 */
public class QueryRunnerProvider {
    private static QueryRunnerProvider ourInstance = new QueryRunnerProvider();
    private QueryRunner runner;
    private QueryIndexRunner indexRunner;
    private ClusterAnalyseService clusterAnalyseService;

    private QueryRunnerProvider() {
    }

    public static QueryRunnerProvider getInstance() {
        return ourInstance;
    }

    public void registerRunner(QueryRunner runner) {
        this.runner = runner;
    }

    public SwiftResultSet executeQuery(QueryBean info) throws Exception {
        return runner.getQueryResult(info);
    }

    public SwiftResultSet executeRemoteQuery(String jsonString, SegmentDestination remoteURI) throws SQLException {
        return getClusterAnalyseService().getRemoteQueryResult(jsonString, remoteURI);
    }

    public Map<URI, IndexQuery<ImmutableBitMap>> executeIndexQuery(Table table, Where where) throws Exception {
        return getIndexRunner().getBitMap(table, where);
    }

    public IndexQuery<ImmutableBitMap> executeIndexQuery(Table table, Where where, Segment segment) throws Exception {
        return getIndexRunner().getBitMap(table, where, segment);
    }

    private ClusterAnalyseService getClusterAnalyseService() {
        if (null == clusterAnalyseService) {
            clusterAnalyseService = SwiftContext.get().getBean(ClusterAnalyseService.class);
        }
        return clusterAnalyseService;
    }

    private QueryIndexRunner getIndexRunner() {
        if (null == indexRunner) {
            indexRunner = (QueryIndexRunner) SwiftContext.get().getBean("queryIndexRunner");
        }
        return indexRunner;
    }
}
