package com.fr.bi.cal.analyze.cal.index.loader.cache;

import com.fr.bi.stable.structure.collection.map.lru.LRUWithKConcurrentHashMap;

/**
 * Created by 小灰灰 on 2017/8/1.
 */
public class WidgetDataCacheManager {
    private static WidgetDataCacheManager instance = new WidgetDataCacheManager();
    private LRUWithKConcurrentHashMap<WidgetCacheKey, WidgetCache> cache = new LRUWithKConcurrentHashMap<WidgetCacheKey, WidgetCache>(1000);

    public static WidgetDataCacheManager getInstance(){
        return instance;
    }

    public void put(WidgetCacheKey key, WidgetCache widgetCache){
        cache.putWeakValue(key, widgetCache);
    }

    public WidgetCache get(WidgetCacheKey key){
        return cache.getWeakHashMapValue(key);
    }


    public void clear() {
        cache.clear();
    }
}
