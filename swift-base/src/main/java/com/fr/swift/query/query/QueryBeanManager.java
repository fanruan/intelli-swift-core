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
    private Map<String, Pair<QueryBean, SegmentDestination>> manager = new ConcurrentHashMap<String, Pair<QueryBean, SegmentDestination>>();

    private QueryBeanManager() {
    }

    public static QueryBeanManager getInstance() {
        return instance;
    }

    public Pair<QueryBean, SegmentDestination> getPair(String queryId) {
        return manager.get(queryId);
    }

    public QueryBean getQueryBean(String queryId) {
        Pair<QueryBean, SegmentDestination> pair = manager.get(queryId);
        return pair == null ? null : pair.getKey();
    }

    public void put(String queryId, Pair<QueryBean, SegmentDestination> pair) {
        manager.put(queryId, pair);
    }

    public void put(String queryId, QueryBean queryBean) {
        manager.put(queryId, Pair.<QueryBean, SegmentDestination>of(queryBean, null));
    }

    public void release(String queryId) {
        manager.remove(queryId);
    }
}
