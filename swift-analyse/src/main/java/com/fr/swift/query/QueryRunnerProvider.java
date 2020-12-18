package com.fr.swift.query;

import com.fr.swift.SwiftContext;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.query.IndexQuery;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryIndexRunner;
import com.fr.swift.result.EmptyResultSet;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.ServiceContext;

import java.util.Collection;
import java.util.Map;

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
        try {
            return SwiftContext.get().getBean(ServiceContext.class).getResultResult(QueryBeanFactory.queryBean2String(queryBean));
        } catch (Exception e) {
            e.printStackTrace();
            return EmptyResultSet.INSTANCE;
        }
//        return QueryCacheBuilder.builder().getQueryResultSetCache(queryBean).getSwiftResultSet();
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
