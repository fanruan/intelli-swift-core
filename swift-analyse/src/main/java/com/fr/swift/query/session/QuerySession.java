package com.fr.swift.query.session;

import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.query.cache.Cache;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.result.SwiftResultSetUtils;
import com.fr.swift.query.session.exception.SessionClosedException;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.core.MD5Utils;
import com.fr.swift.util.Closable;
import com.fr.swift.util.IoUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author yee
 * @date 2018/7/17
 */
public class QuerySession implements Session {
    private Map<Object, Cache<?>> cache;
    private ReentrantReadWriteLock storeLock = new ReentrantReadWriteLock();
    private String sessionId;
    private boolean close;
    private long cacheTimeout;

    public QuerySession(long cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
        cache = new ConcurrentHashMap<Object, Cache<?>>();
        sessionId = MD5Utils.getMD5String(new String[]{UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis())});
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public QueryResultSet executeQuery(QueryBean queryInfo) throws Exception {
        if (isClose()) {
            throw new SessionClosedException(sessionId);
        }
        String queryId = queryInfo.getQueryId();
        String jsonString = QueryBeanFactory.queryBean2String(queryInfo);
        Cache<QueryResultSet> resultSetCache = (Cache<QueryResultSet>) cache.get(queryId);
        if (null != resultSetCache && resultSetCache.get() != null && resultSetCache.get().hasNextPage()) {
            resultSetCache.update();
            return SwiftResultSetUtils.toSerializable(queryInfo.getQueryType(), resultSetCache.get());
        }
        QueryResultSet resultSet = query(jsonString);
        // 缓存具有本地上下文状态的resultSet
        Cache<QueryResultSet> cacheObj = new Cache<QueryResultSet>(resultSet);
        cache.put(queryId, cacheObj);
        // 取本地resultSet的一页，得到可序列化的resultSet
        return SwiftResultSetUtils.toSerializable(queryInfo.getQueryType(), resultSet);
    }

    protected QueryResultSet query(String jsonString) throws Exception {
        return QueryBuilder.buildQuery(jsonString).getQueryResult();
    }

    @Override
    public void close() {
        if (close) {
            return;
        }
        close = true;
        cleanCache(true);
    }

    @Override
    public boolean isClose() {
        return close;
    }

    @Override
    public void cleanCache(boolean force) {
        if (null != cache) {
            Iterator<Map.Entry<Object, Cache<?>>> iterator = cache.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<Object, Cache<?>> entry = iterator.next();
                if (force || entry.getValue().getIdle() >= cacheTimeout) {
                    Object value = entry.getValue().get();
                    if (value instanceof Closable) {
                        IoUtil.close((Closable) value);
                    }
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void putObject(Object key, Object value) {
        storeLock.writeLock().lock();
        try {
            cache.put(key, new Cache<Object>(value));
        } finally {
            storeLock.writeLock().unlock();
        }
    }

    @Override
    public Object getObject(Object key) {
        storeLock.readLock().lock();
        try {
            Cache cache = this.cache.get(key);
            if (cache != null) {
                cache.update();
                return cache.get();
            }
        } finally {
            storeLock.readLock().unlock();
        }
        return null;
    }

    @Override
    public void removeObject(Object key) {
        storeLock.writeLock().lock();
        try {
            cache.remove(key);
        } finally {
            storeLock.writeLock().unlock();
        }
    }
}
