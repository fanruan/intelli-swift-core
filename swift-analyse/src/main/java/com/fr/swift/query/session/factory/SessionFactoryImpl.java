package com.fr.swift.query.session.factory;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.query.cache.Cache;
import com.fr.swift.query.session.QuerySession;
import com.fr.swift.query.session.Session;
import com.fr.swift.util.Strings;
import com.fr.swift.util.concurrent.PoolThreadFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2018/6/19
 */
@SwiftBean(name = "swiftQuerySessionFactory")
public class SessionFactoryImpl implements SessionFactory {

    /**
     * session超时默认10分钟
     */
    private static final long DEFAULT_SESSION_TIMEOUT = 10 * 60 * 1000L;
    /**
     * 缓存超时默认是session超时的一半
     */
    private static final long DEFAULT_CACHE_TIMEOUT = DEFAULT_SESSION_TIMEOUT / 2;
    private Map<String, Cache<Session>> sessionMap = new ConcurrentHashMap<String, Cache<Session>>();
    private Map<String, String> queryMap2Session = new ConcurrentHashMap<String, String>();
    private long sessionTimeout = DEFAULT_SESSION_TIMEOUT;
    private long cacheTimeout = DEFAULT_CACHE_TIMEOUT;
    private ScheduledExecutorService sessionClean = new ScheduledThreadPoolExecutor(1, new PoolThreadFactory("SwiftSessionCleanPool"));
    private ScheduledExecutorService cacheClean = new ScheduledThreadPoolExecutor(1, new PoolThreadFactory("SwiftCacheCleanPool"));

    public SessionFactoryImpl() {
        sessionClean.scheduleAtFixedRate(createSessionCleanTask(), sessionTimeout, sessionTimeout, TimeUnit.MILLISECONDS);
        cacheClean.scheduleAtFixedRate(createCacheCleanTask(), cacheTimeout, cacheTimeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public void setCacheTimeout(long cacheTimeout) {
        cacheClean.shutdown();
        this.cacheTimeout = cacheTimeout;
        cacheClean = new ScheduledThreadPoolExecutor(1, new PoolThreadFactory("SwiftCacheCleanPool"));
        cacheClean.scheduleAtFixedRate(createSessionCleanTask(), cacheTimeout, cacheTimeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public void setSessionTimeout(long sessionTimeout) {
        sessionClean.shutdown();
        this.sessionTimeout = sessionTimeout;
        sessionClean = new ScheduledThreadPoolExecutor(1, new PoolThreadFactory("SwiftSessionCleanPool"));
        sessionClean.scheduleAtFixedRate(createSessionCleanTask(), sessionTimeout, sessionTimeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public Session openSession(String queryId) {
        if (Strings.isEmpty(queryId)) {
            return new QuerySession(cacheTimeout);
        }
        String sessionId = queryMap2Session.get(queryId);
        Session session = null;
        if (Strings.isEmpty(sessionId)) {
            session = createSession(queryId);
        } else {
            Cache<Session> cache = sessionMap.get(sessionId);
            if (null == cache) {
                session = createSession(queryId);
            } else {
                session = cache.get();
                if (!session.isClose()) {
                    cache.update();
                } else {
                    session = createSession(queryId);
                }
            }
        }
        return session;
    }

    @Override
    public void setMaxIdle(int maxIdle) {

    }

    private Session createSession(String queryId) {
        Session session = new QuerySession(cacheTimeout);
        String sessionId = session.getSessionId();
        sessionMap.put(sessionId, new Cache<Session>(session));
        queryMap2Session.put(queryId, sessionId);
        return session;
    }

    private Runnable createSessionCleanTask() {
        return new Runnable() {
            @Override
            public void run() {
                Iterator<Map.Entry<String, Cache<Session>>> iterator = sessionMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Cache<Session> sessionCache = iterator.next().getValue();
                    if (null != sessionCache && sessionCache.getIdle() >= sessionTimeout) {
                        iterator.remove();
                        sessionCache.get().close();
                    }
                }
            }
        };
    }

    private Runnable createCacheCleanTask() {
        return new Runnable() {
            @Override
            public void run() {
                Iterator<Map.Entry<String, Cache<Session>>> iterator = sessionMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Cache<Session> sessionCache = iterator.next().getValue();
                    if (null != sessionCache) {
                        sessionCache.get().cleanCache(false);
                    }
                }
            }
        };
    }

    @Override
    public void clear() {
        cacheClean.shutdownNow();
        sessionClean.shutdownNow();
        Iterator<Map.Entry<String, Cache<Session>>> iterator = sessionMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Cache<Session> sessionCache = iterator.next().getValue();
            if (null != sessionCache) {
                iterator.remove();
                sessionCache.get().close();
            }
        }
    }
}
