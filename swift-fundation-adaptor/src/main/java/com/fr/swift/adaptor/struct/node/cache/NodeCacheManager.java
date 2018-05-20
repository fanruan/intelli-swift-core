package com.fr.swift.adaptor.struct.node.cache;

import com.fr.swift.adaptor.struct.node.paging.NodePagingHelper;
import com.fr.swift.structure.lru.LRUWithKConcurrentHashMap;

/**
 * Created by Lyon on 2018/5/20.
 */
public class NodeCacheManager {

    private static final int CACHE_SIZE = 256;
    private static NodeCacheManager manager = new NodeCacheManager();

    private LRUWithKConcurrentHashMap<String, NodePagingHelper> cache = new LRUWithKConcurrentHashMap<String, NodePagingHelper>(CACHE_SIZE);

    public static NodeCacheManager getInstance() {
        return manager;
    }

    private NodeCacheManager() {}

    public <T extends NodePagingHelper> T get(String sessionId) {
        return (T) cache.getWeakHashMapValue(sessionId);
    }

    public <T extends NodePagingHelper> void cache(String sessionId, T nodePagingHelper) {
        cache.putWeakValue(sessionId, nodePagingHelper);
    }
}
