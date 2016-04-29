package com.fr.bi.common.container;

import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BIMapUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * BI的Map容器类。
 * key值缺失处理。
 *
 * @author Connery on 2015/12/7.
 */
public abstract class BIMapContainer<K, V> implements Serializable {
    /**
     * TODO 当前生成XML依据set和get方法来的，而这里如果设为final的话没有set方法
     * TODO 以后通过标签指定，或者依据get方法。再设为final
     */
    protected Map<K, V> container;

    protected BIMapContainer() {
        this.container = initContainer();
        if (this.container == null) {
            throw new NullPointerException();
        }
    }

    /**
     * 子类需要实现该方法
     * 初始化容器。
     *
     * @return 容器的实例
     */
    protected abstract Map<K, V> initContainer();

    /**
     * 获得key的value
     * <p/>
     * 如果key值不存在，先调用一次相应key值缺失的函数。
     *
     * @param key 键
     * @return 值
     * @throws BIKeyAbsentException 经过key不存在函数处理后
     *                              如果依然不存在那么抛错
     */
    protected V getValue(K key) throws BIKeyAbsentException {
        BINonValueUtils.checkNull(key);
        synchronized (container) {
            if (!container.containsKey(key)) {
                V value = generateAbsentValue(key);
                try {
                    if(value!=null) {
                        putKeyValue(key, value);
                    }
                } catch (Exception e) {
                    throw BINonValueUtils.beyondControl();
                }
            }
            if (containsKey(key)) {
                return container.get(key);
            } else {
                throw new BIKeyAbsentException();
            }

        }
    }

    protected void setContainer(Map<K, V> container) {
        this.container = container;
    }

    protected Map<K, V> getContainer() {
        return BIMapUtils.unmodifiedCollection(container);
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
     * @throws BIKeyDuplicateException 如果key存在，抛错
     */
    protected void putKeyValue(K key, V value) throws BIKeyDuplicateException {
        BINonValueUtils.checkNull(key);
        synchronized (container) {
            if (!container.containsKey(key)) {
                container.put(key, value);
            } else {
                throw new BIKeyDuplicateException();
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


    protected void remove(K key) throws BIKeyAbsentException {
        synchronized (container) {
            if (container.containsKey(key)) {
                container.remove(key);
            } else {
                throw new BIKeyAbsentException();
            }
        }
    }

    protected void copyTo(BIMapContainer<K, V> container) {
        if (container != null) {
            synchronized (container) {
                Iterator<Map.Entry<K, V>> entryIt = this.container.entrySet().iterator();
                while (entryIt.hasNext()) {
                    Map.Entry<K, V> entry = entryIt.next();
                    try {
                        container.putKeyValue(entry.getKey(), entry.getValue());
                    } catch (BIKeyDuplicateException e) {
                        BILogger.getLogger().error(e.getMessage(), e);
                        continue;
                    }
                }
            }
        }
    }

    protected Set<K> keySet() {
        synchronized (container) {
            return container.keySet();
        }
    }
}