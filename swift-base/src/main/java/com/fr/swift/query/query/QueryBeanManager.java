package com.fr.swift.query.query;

import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.structure.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Lyon on 2018/6/14.
 */
public class QueryBeanManager {

    private static QueryBeanManager instance = new QueryBeanManager();
    private Map<String, Object> manager = new ConcurrentHashMap<String, Object>();

    private QueryBeanManager() {
    }

    public static QueryBeanManager getInstance() {
        return instance;
    }

    public Pair<String, SegmentDestination> getPair(String queryId) {
        return (Pair<String, SegmentDestination>) manager.get(queryId);
    }

    public QueryBean getQueryBean(String queryId) {
        return (QueryBean) manager.get(queryId);
    }

    public void put(String queryId, QueryBean queryBean) {
        manager.put(queryId, queryBean);
    }

    public void put(String id, Pair<String, SegmentDestination> pair) {
        manager.put(id, pair);
    }

    public void release(String queryId) {
        manager.remove(queryId);
    }
}
