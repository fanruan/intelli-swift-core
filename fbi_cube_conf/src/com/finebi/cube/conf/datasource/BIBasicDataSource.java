package com.finebi.cube.conf.datasource;

import com.fr.bi.common.container.BIMapContainer;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class created on 2016/5/25.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BIBasicDataSource<T, V> extends BIMapContainer<T, V> {


    @BIIgnoreField
    private Set<V> sourceCache = new HashSet<V>();

    @Override
    protected Map<T, V> initContainer() {
        return new HashMap<T, V>();
    }

    protected void initialSourceCache() {
        synchronized (container) {
            if (sourceCache == null) {
                sourceCache = new HashSet<V>();
                for (V value : container.values()) {
                    sourceCache.add(value);
                }
            }
        }
    }

    /**
     * 环境改变
     */
    protected void clear() {
/**
 * Connery：尽量少用生成新对象的方式当clear用。
 * 或者通过generateEmpty类似函数统一处理也不是不可以。但是原来
 * 这里原来是new HashMap，但是最初对象是ConcurrentMap的。
 * 肯定是上面修改成了Concurrent但是这里忘记修改了。
 * 说不定某次调用了一下envChanged，然后就出现了线程问题
 * 这种导致的Bug肯定又很难还原很难解决了。
 */
        super.clear();
        sourceCache.clear();

    }

    protected V getSource(T id) throws BIKeyAbsentException {
        return getValue(id);
    }

    protected void removeSource(T id) throws BIKeyAbsentException {
        synchronized (container) {
            initialSourceCache();
            if (containsKey(id)) {
                V source = getSource(id);
                remove(id);
                removeCacheSource(source);

            }
        }
    }

    private void removeCacheSource(V source) {
        synchronized (container) {
            sourceCache.remove(source);
        }
    }

    protected void addSource(T id, V source) throws BIKeyDuplicateException {
        synchronized (container) {
            initialSourceCache();
            if (cacheContainSource(source)) {
                putKeyValue(id, getSpecificCacheSource(source));
            } else {
                putKeyValue(id, source);
                addSourceCache(source);
            }
        }
    }

    protected void addSourceCache(V source) {
        synchronized (container) {
            sourceCache.add(source);
        }
    }

    protected boolean cacheContainSource(V source) {
        synchronized (container) {
            return sourceCache.contains(source);
        }
    }

    protected V getSpecificCacheSource(V sourceTarget) {
        synchronized (container) {
            for (V source : sourceCache) {
                if (isEqual(source, sourceTarget)) {
                    return source;
                }
            }
            return null;
        }
    }

    abstract boolean isEqual(V firstSource, V secondSource);

    /**
     * 修改md5表
     */
    protected void editSource(T id, V source) throws BIKeyDuplicateException, BIKeyAbsentException {
        synchronized (container) {
            initialSourceCache();
            removeSource(id);
            addSource(id, source);
        }
    }

    public boolean contain(T id) {
        return containsKey(id);
    }

}