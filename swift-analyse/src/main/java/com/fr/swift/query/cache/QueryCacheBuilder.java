package com.fr.swift.query.cache;

import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.service.ServiceContext;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @version 1.1
 * Created by yee on 2019-09-02
 */
public class QueryCacheBuilder {
    /**
     * session超时默认5分钟
     */
    private static final int DEFAULT_TIMEOUT = 5;

    private ConcurrentHashMap<String, QueryCache> cacheContainer;

    private QueryCacheBuilder() {
        this.cacheContainer = new ConcurrentHashMap<>();
        ScheduledExecutorService queryCacheClean = SwiftExecutors.newScheduledThreadPool(1, new PoolThreadFactory("SwiftQueryCacheCleanPool"));
        queryCacheClean.scheduleAtFixedRate(createCleanTask(), DEFAULT_TIMEOUT, DEFAULT_TIMEOUT, TimeUnit.MINUTES);
    }

    public static QueryCacheBuilder builder() {
        return Singleton.BUILDER;
    }

    private Runnable createCleanTask() {
        return new Runnable() {
            @Override
            public void run() {
                final Iterator<Map.Entry<String, QueryCache>> iterator = cacheContainer.entrySet().iterator();
                while (iterator.hasNext()) {
                    final QueryCache value = iterator.next().getValue();
                    if (value.getIdle() > TimeUnit.MINUTES.toMillis(DEFAULT_TIMEOUT)) {
                        value.clear();
                    }
                    iterator.remove();
                }
            }
        };
    }

    public QueryCache getOrBuildCache(QueryBean queryBean) {
        SwiftLoggers.getLogger().debug("get queryCache [{}]!", queryBean.getQueryId());
        final QueryCache value = new QueryCache(queryBean, q -> {
            try {
                final ServiceContext serviceContext = ProxySelector.getProxy(ServiceContext.class);
                return serviceContext.getQueryResult(QueryBeanFactory.queryBean2String(q));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        QueryCache queryCache = cacheContainer.putIfAbsent(queryBean.getQueryId(), value);
        queryCache = queryCache == null ? value : queryCache;
        queryCache.update();
        return queryCache;
    }

    public void removeCache(String queryId) {
        SwiftLoggers.getLogger().debug("remove queryCache [{}]!", queryId);
        final QueryCache remove = cacheContainer.remove(queryId);
        if (null != remove) {
            SwiftLoggers.getLogger().debug("clear queryCache [{}]!", queryId);
            remove.clear();
        }
    }

    private static class Singleton {
        private static QueryCacheBuilder BUILDER = new QueryCacheBuilder();
    }
}
