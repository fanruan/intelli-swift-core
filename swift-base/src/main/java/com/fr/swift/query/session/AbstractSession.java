package com.fr.swift.query.session;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.cache.Cache;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.session.exception.SessionClosedException;
import com.fr.swift.result.serialize.SwiftResultSetUtils;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.core.MD5Utils;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/6/19
 */
public abstract class AbstractSession implements Session {

    protected SwiftLogger logger = SwiftLoggers.getLogger(this.getClass());

    private Map<String, Cache<? extends SwiftResultSet>> cache;
    private String sessionId;
    private boolean close;
    private long cacheTimeout;

    protected AbstractSession(long cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
        cache = new ConcurrentHashMap<String, Cache<? extends SwiftResultSet>>();
        sessionId = MD5Utils.getMD5String(new String[]{UUID.randomUUID().toString(), String.valueOf(System.currentTimeMillis())});
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public SwiftResultSet executeQuery(QueryBean queryInfo) throws Exception {
        if (isClose()) {
            throw new SessionClosedException(sessionId);
        }
        String queryId = queryInfo.getQueryId();
        Cache<? extends SwiftResultSet> resultSetCache = cache.get(queryId);
        if (null != resultSetCache) {
            resultSetCache.update();
            return SwiftResultSetUtils.convert2Serializable(queryId, queryInfo.getQueryType(), resultSetCache.get());
        }
        // 缓存具有本地上下文状态的resultSet
        SwiftResultSet resultSet = query(queryInfo);
        Cache<SwiftResultSet> cacheObj = new Cache<SwiftResultSet>(resultSet);
        cache.put(queryId, cacheObj);
        // 取本地resultSet的一个快照，得到可序列化的resultSet
        return SwiftResultSetUtils.convert2Serializable(queryId, queryInfo.getQueryType(), resultSet);
    }

    protected abstract SwiftResultSet query(QueryBean queryInfo) throws Exception;

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
            Iterator<Map.Entry<String, Cache<? extends SwiftResultSet>>> iterator = cache.entrySet().iterator();

            while (iterator.hasNext()) {
                try {
                    Map.Entry<String, Cache<? extends SwiftResultSet>> entry = iterator.next();
                    if (force || entry.getValue().getIdle() >= cacheTimeout) {
                        entry.getValue().get().close();
                        iterator.remove();
                    }
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
    }
}
