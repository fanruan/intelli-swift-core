package com.fr.swift.cache;

import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2018/4/2
 *
 * @author Lucifer
 * @description 临时存放datasource
 * todo 接入MetaDataConfig
 * @since Advanced FineBI Analysis 1.0
 */
public class SourceCache {

    private Map<SourceKey, DataSource> key2SourceMap = new ConcurrentHashMap<SourceKey, DataSource>();

    private static class SingletonHolder {
        private static final SourceCache INSTANCE = new SourceCache();
    }

    public static final SourceCache getCache() {
        return SourceCache.SingletonHolder.INSTANCE;
    }

    /**
     * 优先去cache取datasource，防止initMetaData操作过多。
     * todo 提供主动和自动清除的功能，例如加入过期时间。
     *
     * @param dataSource
     * @return
     */
    public DataSource getMetaDataBySource(DataSource dataSource) {
        SourceKey sourceKey = dataSource.getSourceKey();
        if (key2SourceMap.containsKey(sourceKey)) {
            return key2SourceMap.get(sourceKey);
        }
        putSource2MetaData(dataSource);
        return dataSource;
    }

    public void putSource2MetaData(DataSource dataSource) {
        key2SourceMap.put(dataSource.getSourceKey(), dataSource);
    }
}

