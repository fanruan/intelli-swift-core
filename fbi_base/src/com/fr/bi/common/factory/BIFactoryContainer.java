package com.fr.bi.common.factory;

import com.fr.bi.common.container.BIMapContainer;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Connery on 2015/12/7.
 */
class BIFactoryContainer<V> extends BIMapContainer<String, V> {

    @Override
    protected V generateAbsentValue(String key) {
        return null;
    }

    @Override
    protected Map<String, V> initContainer() {
        return new HashMap<String, V>();
    }

    @Override
    public V getValue(String key) {
        try {
            return super.getValue(key);
        } catch (BIKeyAbsentException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    protected BIFactoryContainer() {
        super();
    }

    @Override
    protected void setContainer(Map<String, V> container) {
        super.setContainer(container);
    }

    @Override
    protected Map<String, V> getContainer() {
        return super.getContainer();
    }

    @Override
    protected void putKeyValue(String key, V value) throws BIKeyDuplicateException {
        super.putKeyValue(key, value);
    }

    @Override
    protected Boolean containsKey(String key) {
        return super.containsKey(key);
    }

    @Override
    protected void clear() {
        super.clear();
    }

    @Override
    protected void remove(String key) throws BIKeyAbsentException {
        super.remove(key);
    }

    @Override
    protected void copyTo(BIMapContainer<String, V> container) {
        super.copyTo(container);
    }
}