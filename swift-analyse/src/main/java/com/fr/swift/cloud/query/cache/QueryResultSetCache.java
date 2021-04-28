package com.fr.swift.cloud.query.cache;

import com.fr.swift.cloud.query.QueryRunnerProvider;
import com.fr.swift.cloud.query.builder.QueryBuilder;
import com.fr.swift.cloud.query.query.QueryBean;
import com.fr.swift.cloud.query.result.serialize.QueryResultSetSerializer;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.result.qrs.QueryResultSet;
import com.fr.swift.cloud.service.AnalyseService;
import com.fr.swift.cloud.util.IoUtil;
import com.fr.swift.cloud.util.function.Function;


/**
 * @author yee
 * @version 1.1
 * Created by yee on 2019-09-02
 */
public class QueryResultSetCache implements QueryCache {
    private QueryBean queryBean;
    private SwiftResultSet resultSet;
    private QueryResultSet queryResultSet;
    private Function<QueryBean, QueryResultSet> function;
    private long createTime;

    public QueryResultSetCache(QueryBean queryBean, Function<QueryBean, QueryResultSet> function) {
        this.queryBean = queryBean;
        this.function = function;
        this.createTime = System.currentTimeMillis();
    }

    @Override
    public long getIdle() {
        return System.currentTimeMillis() - this.createTime;
    }

    @Override
    public void update() {
        createTime = System.currentTimeMillis();
    }

    /**
     * QueryRunnerProvider中使用，返回SwiftResultSet
     *
     * @return SwiftResultSet
     * @see QueryRunnerProvider
     */
    @Override
    public SwiftResultSet getSwiftResultSet() {
        if (null == resultSet) {
            resultSet = QueryResultSetSerializer.toSwiftResultSet(function.apply(queryBean), queryBean);
        }
        return resultSet;
    }

    /**
     * 在AnalyseService中使用，返回的是序列化的QueryResultSet
     *
     * @return 序列化的QueryResultSet
     * @throws Exception
     * @see AnalyseService
     */
    public QueryResultSet getQueryResultSet() throws Exception {
        if (null == queryResultSet) {
            queryResultSet = QueryBuilder.buildQuery(queryBean).getQueryResult();
        }
        return QueryResultSetSerializer.serialize(queryBean.getQueryType(), queryResultSet);
    }

    @Override
    public void clear() {
        IoUtil.close(resultSet);
    }
}
