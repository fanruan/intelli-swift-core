package com.fr.swift.query.session;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.query.cache.Cache;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.result.SwiftResultSetUtils;
import com.fr.swift.query.session.exception.SessionClosedException;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.core.MD5Utils;

import java.io.Closeable;
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
    private Map<String, Cache<? extends QueryResultSet>> cache;
    private Map store = new ConcurrentHashMap();
    private ReentrantReadWriteLock storeLock = new ReentrantReadWriteLock();
    private String sessionId;
    private boolean close;
    private long cacheTimeout;

    public QuerySession(long cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
        cache = new ConcurrentHashMap<String, Cache<? extends QueryResultSet>>();
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
        Cache<? extends QueryResultSet> resultSetCache = cache.get(queryId);
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
        cleanStore();
    }

    @Override
    public boolean isClose() {
        return close;
    }

    @Override
    public void cleanCache(boolean force) {
        if (null != cache) {
            Iterator<Map.Entry<String, Cache<? extends QueryResultSet>>> iterator = cache.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, Cache<? extends QueryResultSet>> entry = iterator.next();
                if (force || entry.getValue().getIdle() >= cacheTimeout) {
                    entry.getValue().get().close();
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void cleanStore() {
        storeLock.writeLock().lock();
        try {
            Iterator<Map.Entry> it = store.entrySet().iterator();
            while (it.hasNext()) {
                Object obj = it.next().getValue();
                it.remove();
                try {
                    if (null != obj && obj instanceof Closeable) {
                        ((Closeable) obj).close();
                    }
                } catch (Exception ignore) {
                    SwiftLoggers.getLogger().warn("session close ignore exception");
                }
            }
        } finally {
            storeLock.writeLock().unlock();
        }
    }

    @Override
    public void putObject(Object key, Object value) {
        storeLock.writeLock().lock();
        try {
            store.put(key, value);
        } finally {
            storeLock.writeLock().unlock();
        }
    }

    @Override
    public Object getObject(Object key) {
        storeLock.readLock().lock();
        try {
            return store.get(key);
        } finally {
            storeLock.readLock().unlock();
        }
    }

    @Override
    public void removeObject(Object key) {
        storeLock.writeLock().lock();
        try {
            store.remove(key);
        } finally {
            storeLock.writeLock().unlock();
        }
    }
}
