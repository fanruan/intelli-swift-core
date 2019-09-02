package com.fr.swift.query.cache;

import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.result.serialize.QueryResultSetSerializer;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.util.Clearable;
import com.fr.swift.util.IoUtil;
import com.fr.swift.util.function.Function;


/**
 * @author yee
 * @version 1.1
 * Created by yee on 2019-09-02
 */
public class QueryCache implements Clearable {
    private QueryBean queryBean;
    private SwiftResultSet resultSet;
    private QueryResultSet queryResultSet;
    private Function<QueryBean, QueryResultSet> function;
    private long createTime;

    public QueryCache(QueryBean queryBean, Function<QueryBean, QueryResultSet> function) {
        this.queryBean = queryBean;
        this.function = function;
        this.createTime = System.currentTimeMillis();
    }

    public long getIdle() {
        return System.currentTimeMillis() - this.createTime;
    }

    public void update() {
        createTime = System.currentTimeMillis();
    }

    public SwiftResultSet getSwiftResultSet() {
        return null != resultSet ? resultSet : doQuery();
    }

    public QueryResultSet getQueryResultSet() throws Exception {
        if (null == queryResultSet) {
            queryResultSet = QueryBuilder.buildQuery(queryBean).getQueryResult();
        }
        return QueryResultSetSerializer.serialize(queryBean.getQueryType(), queryResultSet);
    }

    private SwiftResultSet doQuery() {
        this.resultSet = QueryResultSetSerializer.toSwiftResultSet(function.apply(queryBean), queryBean);
        return this.resultSet;
    }

    @Override
    public void clear() {
        IoUtil.close(resultSet);
    }
}
