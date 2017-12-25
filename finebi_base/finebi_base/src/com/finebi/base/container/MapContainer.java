package com.finebi.base.container;

import com.finebi.base.exception.KeyAbsentException;
import com.finebi.base.exception.KeyDuplicateException;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class created on 2017/9/29.
 *
 * @author Each.Zhang
 */
public abstract class MapContainer<K, V> implements Serializable {

    private static final long serialVersionUID = -5655177173038553296L;

    protected Map<K, V> container;

    protected MapContainer() {

        this.container = initContainer();
        if (this.container == null) {
            throw new NullPointerException();
        }
    }

    /**
     * 初始化容器
     *
     * @return
     */
    protected abstract Map<K, V> initContainer();

    protected V getValue(K key) throws KeyAbsentException {
        //BINonValueUtils.checkNull(key);
        synchronized (container) {
            if (!container.containsKey(key)) {
                V value = generateAbsentValue(key);
                try {
                    if (value != null) {
                        putKeyValue(key, value);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            if (containsKey(key)) {
                return container.get(key);
            } else {
                throw new KeyAbsentException();
            }
        }
    }

    /**
     * 处理key值不存在的情况。
     * 子类需要实现该方法
     * 在调用getValue方法的时候，如果key值不存在的情况下
     * 调用该方法处理.
     *
     * @param key 该key值不存在
     * @return
     */
    protected abstract V generateAbsentValue(K key);

    /**
     * 添加一对键值
     *
     * @param key   键
     * @param value 值
     * @throws KeyDuplicateException 如果key存在，抛错
     */
    protected void putKeyValue(K key, V value) throws KeyDuplicateException {
        //BINonValueUtils.checkNull(key);
        synchronized (container) {
            if (!container.containsKey(key)) {
                container.put(key, value);
            } else {
                throw new KeyDuplicateException();
            }
        }
    }

    protected Boolean containsKey(K key) {

        synchronized (container) {
            return container.containsKey(key);
        }
    }

    protected void clear() {

        synchronized (container) {
            container.clear();
        }
    }

    protected void remove(K key) throws KeyAbsentException {

        synchronized (container) {
            if (container.containsKey(key)) {
                container.remove(key);
            } else {
                throw new KeyAbsentException();
            }
        }
    }

    protected void copyTo(MapContainer<K, V> container) {

        if (container != null) {
            synchronized (container) {
                Iterator<Map.Entry<K, V>> entryIt = this.container.entrySet().iterator();
                while (entryIt.hasNext()) {
                    Map.Entry<K, V> entry = entryIt.next();
                    try {
                        container.putKeyValue(entry.getKey(), entry.getValue());
                    } catch (KeyDuplicateException e) {
                        //com.finebi.log.BILoggerFactory.getLogger().error(e.getMessage(), e);
                        continue;
                    }
                }
            }
        }
    }

    protected Map<K, V> getContainer() {

        return container;
    }

    protected Set<K> keySet() {

        synchronized (container) {
            return container.keySet();
        }
    }

}
