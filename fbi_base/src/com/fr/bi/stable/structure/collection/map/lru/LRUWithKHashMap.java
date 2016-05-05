/**
 *
 */
package com.fr.bi.stable.structure.collection.map.lru;

import com.fr.bi.stable.structure.collection.map.ConcurrentCacheHashMap;
import com.fr.bi.common.inter.Release;
import com.fr.bi.common.inter.ValueCreator;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.Map;


/**
 * @author Daniel
 *         LRU不适合放需要释放的对象
 *         LRU-K的双队列 缓存
 *         数据首先会放入FIFO队列
 *         当FIFO队列的访问次数达到K次时则放入LRU优先队列
 */
public class LRUWithKHashMap<K, V> implements Release {
    /**
     * LRU K值，每次访问存在多次情况 K值稍大
     */
    private static final int LRU_K = 3;
    /**
     * 队列
     */
    private Map<K, MemoryCountObject<V>> fifo;

    /**
     * LRU
     */
    private LRUHashMap<K, V> lru;

    public LRUWithKHashMap() {
        fifo = createFIFOMap(-1);
        ;
        lru = new LRUHashMap<K, V>();
    }

    /**
     * 分别构造不同大小的缓存
     *
     * @param fifoSize fifo队列大小
     * @param lru_size lru队列大小
     */
    public LRUWithKHashMap(int fifoSize, int lru_size) {
        fifo = createFIFOMap(fifoSize);
        lru = new LRUHashMap<K, V>(lru_size);
    }

    /**
     * 构造两个大小相等的缓存
     *
     * @param size 大小
     */
    public LRUWithKHashMap(int size) {
        this(size, size);
    }

    private Map<K, MemoryCountObject<V>> createFIFOMap(int i) {
        if (i == -1) {
            return new ConcurrentCacheHashMap<K, MemoryCountObject<V>>();
        }
        return new ConcurrentCacheHashMap<K, MemoryCountObject<V>>(i);
    }

    protected MemoryCountObject<V> put(K k, V v) {

        synchronized (this) {
            return fifo.put(k, new MemoryCountObject<V>(v));
        }

    }

    /**
     * 存储弱引用
     *
     * @param k key值
     * @param v value值
     * @return 存储内容
     */
    public MemoryCountObject<V> putWeakValue(K k, V v) {
        return put(k, v);
    }

    /**
     * 获取弱引用对象
     *
     * @param k key值
     * @return 对象V
     */
    public V getWeakHashMapValue(K k) {
        return getValue(k);
    }

    /**
     * 获取map的对象，creater不为空的情况下 get不到值则使用creater创建对象并放入队列
     *
     * @param k       key值
     * @param creater 可创建对象V的接口
     * @return 值
     */
    public V get(K k, ValueCreator<V> creater) {
        V v = getValue(k);
        if (v != null) {
            return v;
        }
        if (creater == null) {
            return v;
        }
        try {
            v = creater.createNewObject();
        } catch (Exception e) {
                    BILogger.getLogger().error(e.getMessage(), e);
        }
        if (v != null) {
            put(k, v);
        }
        return v;
    }

    protected V getValue(K k) {
        V v = lru.get(k);
        if (v != null) {
            return v;
        }
        MemoryCountObject<V> mco = fifo.get(k);
        if (mco != null) {
            v = mco.getValue();
            mco.access_plus();
            if (v != null && mco.access_count() > LRU_K) {
                fifo.remove(k);
                lru.put(k, v);
                return v;
            }
        }
        return v;
    }

    /**
     * 删除Key和对应的Value
     *
     * @param key 键值
     */
    public void remove(K key) {
        if (lru.containsKey(key)) {
            lru.remove(key);
        } else {

        }
        if (fifo.containsKey(key)) {
            fifo.remove(key);
        } else {

        }
    }

    /**
     * 如果key存在，删除原有的value，重新create新值，同时返回新值
     * 如果不存在，则按照get的方法执行。
     *
     * @param creater 可创建对象V的接口
     * @return 值
     */
    public V update(K key, ValueCreator<V> creater) {
        synchronized (this) {
            remove(key);
            return get(key, creater);
        }
    }

    /**
     * 是否包含Key值
     *
     * @param k 对应的key值
     * @return 布尔结果
     */
    public boolean LRUcontain(K k) {
        if (lru != null) {
            return lru.containsKey(k);
        } else {
            return false;
        }
    }

    public boolean FIFOcontain(K k) {
        if (fifo != null) {
            return fifo.containsKey(k);
        } else {
            return false;
        }
    }

    /**
     * 调整大小
     *
     * @param size
     */
    public void setSize(int size) {
        synchronized (this) {
            FIFOHashMap<K, MemoryCountObject<V>> fifo = new FIFOHashMap<K, MemoryCountObject<V>>(size);
            fifo.putAll(this.fifo);
            this.fifo.clear();
            this.fifo = fifo;
            LRUHashMap<K, V> lru = new LRUHashMap<K, V>(size);
            lru.putAll(this.lru);
            this.lru.clear();
            this.lru = lru;
        }
    }

    /**
     * 资源释放
     */
    @Override
    public void releaseResource() {
        fifo.clear();
        lru.clear();
    }


}