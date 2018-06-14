package com.fr.swift.query.query;

import com.fr.swift.source.SwiftResultSet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Lyon on 2018/6/14.
 */
public class QueryResultSetManager {

    private static QueryResultSetManager instance = new QueryResultSetManager();
    private Map<String, SwiftResultSet> manager = new ConcurrentHashMap<String, SwiftResultSet>();

    private QueryResultSetManager() {
    }

    public static QueryResultSetManager getInstance() {
        return instance;
    }

    /**
     * 根据查询id查找查询结果集
     *
     * @param queryId
     * @return
     */
    public SwiftResultSet get(String queryId) {
        return manager.get(queryId);
    }

    /**
     * 缓存结果集
     *
     * @param queryId
     * @param resultSet
     */
    public void put(String queryId, SwiftResultSet resultSet) {
        manager.put(queryId, resultSet);
    }

    public void release(String queryId) {
        // TODO: 2018/6/14 清除缓存结果集
        manager.remove(queryId);
    }
}
