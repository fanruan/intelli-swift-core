package com.fr.swift.query.result;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.util.IoUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pony
 * @date 2017/11/27
 * 处理结果的query
 */
public abstract class AbstractResultQuery<T extends QueryResultSet<?>> implements Query<T>, QueryResultSetMerger<T> {

    protected int fetchSize;
    /**
     * ResultQuery是由多个query合并来的
     */
    protected List<Query<T>> queries;

    protected AbstractResultQuery(int fetchSize, List<Query<T>> queries) {
        this.fetchSize = fetchSize;
        this.queries = queries;
    }

    @Override
    public T getQueryResult() throws SQLException {
        List<T> resultSets = new ArrayList<T>(queries.size());
        try {
            for (Query<T> query : queries) {
                try {
                    resultSets.add(query.getQueryResult());
                } catch (SQLException e) {
                    SwiftLoggers.getLogger().info("segment query error: {}", query, e);
                }
            }
            return merge(resultSets);
        } catch (Exception e) {
            IoUtil.close(resultSets);
            throw new SQLException(e);
        }
    }
}
