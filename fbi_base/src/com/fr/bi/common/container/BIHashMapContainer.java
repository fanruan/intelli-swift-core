package com.fr.bi.common.container;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Connery on 2015/12/28.
 */
public class BIHashMapContainer<K, V> extends BIMapContainer<K, V> {
    @Override
    protected Map<K, V> initContainer() {
        return new HashMap<K, V>();
    }

    @Override
    protected V generateAbsentValue(K key) {
        return null;
    }
}