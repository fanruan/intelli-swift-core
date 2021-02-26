package com.fr.swift.query.cache;

import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.result.serialize.QueryResultSetSerializer;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.util.IoUtil;
import com.fr.swift.util.function.Function;


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
     * @see com.fr.swift.query.QueryRunnerProvider
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
     * @see com.fr.swift.service.AnalyseService
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
