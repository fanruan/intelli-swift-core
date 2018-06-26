package com.fr.swift.query.query;

import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.structure.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * todo 初步想法有待分析改进
 * Created by Lyon on 2018/6/14.
 */
public class RemoteQueryInfoManager {

    private static RemoteQueryInfoManager instance = new RemoteQueryInfoManager();
    private Map<String, Pair<QueryBean, SegmentDestination>> manager = new ConcurrentHashMap<String, Pair<QueryBean, SegmentDestination>>();

    private RemoteQueryInfoManager() {
    }

    public static RemoteQueryInfoManager getInstance() {
        return instance;
    }

    public Pair<QueryBean, SegmentDestination> get(String queryId) {
        return manager.get(queryId);
    }

    public void put(String queryId, Pair<QueryBean, SegmentDestination> pair) {
        manager.put(queryId, pair);
    }

    public void release(String queryId) {
        manager.remove(queryId);
    }
}
