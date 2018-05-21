package com.fr.swift.adaptor.struct.node.cache;

import com.fr.swift.adaptor.struct.node.paging.PagingSession;
import com.fr.swift.structure.lru.LRUWithKConcurrentHashMap;

/**
 * Created by Lyon on 2018/5/21.
 */
public class PagingSessionCacheManager {

    private static final int SESSION_SIZE = 256;
    private static final PagingSessionCacheManager manager = new PagingSessionCacheManager();

    private LRUWithKConcurrentHashMap<String, PagingSession> cache = new LRUWithKConcurrentHashMap<String, PagingSession>(SESSION_SIZE);

    public static PagingSessionCacheManager getInstance() {
        return manager;
    }

    private PagingSessionCacheManager() {}

    public PagingSession get(String sessionId) {
        return sessionId == null ? null : cache.getWeakHashMapValue(sessionId);
    }

    public void cache(String sessionId, PagingSession pagingSession) {
        if (sessionId == null) {
            return;
        }
        cache.putWeakValue(sessionId, pagingSession);
    }
}
