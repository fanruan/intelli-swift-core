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
    private Map<String, Pair<QueryInfo, SegmentDestination>> manager = new ConcurrentHashMap<String, Pair<QueryInfo, SegmentDestination>>();

    private RemoteQueryInfoManager() {
    }

    public static RemoteQueryInfoManager getInstance() {
        return instance;
    }

    public Pair<QueryInfo, SegmentDestination> get(String queryId) {
        return manager.get(queryId);
    }

    public void put(String queryId, Pair<QueryInfo, SegmentDestination> pair) {
        manager.put(queryId, pair);
    }

    public void release(String queryId) {
        manager.remove(queryId);
    }
}
