package com.fr.swift.cloud.query;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.db.Table;
import com.fr.swift.cloud.db.Where;
import com.fr.swift.cloud.query.cache.QueryCacheBuilder;
import com.fr.swift.cloud.query.info.bean.parser.QueryInfoParser;
import com.fr.swift.cloud.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.cloud.query.info.bean.query.QueryInfoBean;
import com.fr.swift.cloud.query.info.detail.DetailQueryInfo;
import com.fr.swift.cloud.query.query.IndexQuery;
import com.fr.swift.cloud.query.query.QueryBean;
import com.fr.swift.cloud.query.query.QueryIndexRunner;
import com.fr.swift.cloud.query.query.QueryType;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.segment.SegmentKey;

import java.util.Collection;
import java.util.Map;

import static com.fr.swift.cloud.query.query.QueryType.DETAIL;

/**
 * @author pony
 * @date 2017/12/20
 */
public class QueryRunnerProvider {
    private static QueryRunnerProvider ourInstance = new QueryRunnerProvider();
    private QueryIndexRunner indexRunner;

    private QueryRunnerProvider() {
    }

    public static QueryRunnerProvider getInstance() {
        return ourInstance;
    }

    public SwiftResultSet query(QueryBean queryBean) {
        QueryType queryType = queryBean.getQueryType();
        // todo::分组和排序暂未优化，先把不排序明细优化打通，单独走这条路
        if (queryType == DETAIL) {
            DetailQueryInfo queryInfo = (DetailQueryInfo) QueryInfoParser.parse((QueryInfoBean) queryBean);
            if (!queryInfo.hasSort()) {
                return QueryCacheBuilder.builder().getCalcResultSetCache(queryBean).getSwiftResultSet();
            }
        }
        return QueryCacheBuilder.builder().getQueryResultSetCache(queryBean).getSwiftResultSet();
    }

    public SwiftResultSet query(String queryJson) throws Exception {
        QueryBean queryBean = QueryBeanFactory.create(queryJson);
        return query(queryBean);
    }

    public Map<SegmentKey, IndexQuery<ImmutableBitMap>> executeIndexQuery(Table table, Where where) throws Exception {
        return getIndexRunner().getBitMap(table, where);
    }

    public IndexQuery<ImmutableBitMap> executeIndexQuery(Table table, Where where, SegmentKey segmentKey) throws Exception {
        return getIndexRunner().getBitMap(table, where, segmentKey);
    }

    public Collection<SegmentKey> executeSegmentsQuery(Table table, Where where) throws Exception {
        return getIndexRunner().getWhereSegments(table, where);
    }

    private QueryIndexRunner getIndexRunner() {
        if (null == indexRunner) {
            indexRunner = (QueryIndexRunner) SwiftContext.get().getBean("queryIndexRunner");
        }
        return indexRunner;
    }
}
