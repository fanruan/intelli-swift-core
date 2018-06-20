package com.fr.swift.adaptor.struct.node.cache;

import com.fr.swift.adaptor.struct.node.paging.NodePagingHelper;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.structure.lru.LRUWithKConcurrentHashMap;

/**
 * Created by Lyon on 2018/5/20.
 */
public class NodeCacheManager {

    private static final int CACHE_SIZE = 256;
    private static NodeCacheManager manager = new NodeCacheManager();

    private LRUWithKConcurrentHashMap<QueryInfo, NodePagingHelper> cache = new LRUWithKConcurrentHashMap<QueryInfo, NodePagingHelper>(CACHE_SIZE);

    public static NodeCacheManager getInstance() {
        return manager;
    }

    private NodeCacheManager() {}

    public <T extends NodePagingHelper> T get(QueryInfo queryInfo) {
        return (T) cache.getWeakHashMapValue(queryInfo);
    }

    public <T extends NodePagingHelper> void cache(QueryInfo queryInfo, T nodePagingHelper) {
        cache.putWeakValue(queryInfo, nodePagingHelper);
    }
}
