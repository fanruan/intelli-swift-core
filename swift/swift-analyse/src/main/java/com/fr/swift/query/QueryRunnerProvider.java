package com.fr.swift.query;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.query.IndexQuery;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryIndexRunner;
import com.fr.swift.query.result.SwiftResultSetUtils;
import com.fr.swift.query.session.Session;
import com.fr.swift.query.session.factory.SessionFactory;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.util.Strings;

import java.net.URI;
import java.util.Map;

/**
 * @author pony
 * @date 2017/12/20
 */
public class QueryRunnerProvider {
    private static QueryRunnerProvider ourInstance = new QueryRunnerProvider();
    private QueryIndexRunner indexRunner;
    private SessionFactory sessionFactory = SwiftContext.get().getBean("swiftQuerySessionFactory", SessionFactory.class);

    private QueryRunnerProvider() {
    }

    public static QueryRunnerProvider getInstance() {
        return ourInstance;
    }

    public SwiftResultSet query(QueryBean queryBean) throws Exception {
        return query(QueryBeanFactory.queryBean2String(queryBean));
    }

    public SwiftResultSet query(String queryJson) throws Exception {
        QueryBean queryBean = QueryBeanFactory.create(queryJson);
        String queryId = queryBean.getQueryId();
        SwiftResultSet resultSet;
        if (Strings.isNotEmpty(queryId)) {
            Session session = sessionFactory.openSession(queryId);
            resultSet = (SwiftResultSet) session.getObject(queryId);
            if (resultSet != null) {
                return resultSet;
            }
        }
        AnalyseService service = ProxySelector.getInstance().getFactory().getProxy(AnalyseService.class);
        resultSet = SwiftResultSetUtils.toSwiftResultSet(
                service.getQueryResult(QueryBeanFactory.queryBean2String(queryBean)), queryBean);
        if (Strings.isNotEmpty(queryId)) {
            sessionFactory.openSession(queryId).putObject(queryId, resultSet);
        }
        return resultSet;
    }

    public Map<URI, IndexQuery<ImmutableBitMap>> executeIndexQuery(Table table, Where where) throws Exception {
        return getIndexRunner().getBitMap(table, where);
    }

    public IndexQuery<ImmutableBitMap> executeIndexQuery(Table table, Where where, Segment segment) throws Exception {
        return getIndexRunner().getBitMap(table, where, segment);
    }

    private QueryIndexRunner getIndexRunner() {
        if (null == indexRunner) {
            indexRunner = (QueryIndexRunner) SwiftContext.get().getBean("queryIndexRunner");
        }
        return indexRunner;
    }
}
