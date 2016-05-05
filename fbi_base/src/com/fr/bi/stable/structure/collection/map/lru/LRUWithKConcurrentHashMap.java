/**
 *
 */
package com.fr.bi.stable.structure.collection.map.lru;

import com.fr.bi.common.inter.ValueCreator;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Daniel
 *         LRU不适合放需要释放的对象,不适合放数据很大的对象比如index之类 index之类请用他的父对象父对象不适合放create里面需要生成index的对象
 */
public class LRUWithKConcurrentHashMap<K, V> extends LRUWithKHashMap<K, V> {

    private Map<K, Object> lockMap = new ConcurrentHashMap<K, Object>();

    public LRUWithKConcurrentHashMap(int cacheSize) {
        super(cacheSize);
    }

    @Override
    public V get(K k, ValueCreator<V> creater) {
        V v = getValue(k);
        if (v != null) {
            return v;
        }
        if (creater == null) {
            return v;
        }
        Object lock = lockMap.get(k);
        if (lock == null) {
            synchronized (this) {
                if (lock == null) {
                    lock = new Object();
                    lockMap.put(k, lock);
                }
            }
        }
        synchronized (lock) {
            v = getValue(k);
            if (v == null) {
                try {
                    v = creater.createNewObject();
                } catch (Exception e) {
                            BILogger.getLogger().error(e.getMessage(), e);
                }
                if (v != null) {
                    put(k, v);
                }
            }
        }
        return v;
    }


    @Override
    public void remove(K key) {
        super.remove(key);
        lockMap.remove(key);
    }

    /**
     * 资源释放
     */
    @Override
    public void releaseResource() {
        super.releaseResource();
        lockMap.clear();
    }


}