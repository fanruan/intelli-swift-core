package com.fr.bi.common.container;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Connery on 2015/12/28.
 */
public class BITreeMapContainer<K, V> extends BIMapContainer<K, V> {
    @Override
    protected Map<K, V> initContainer() {
        return new TreeMap<K, V>();
    }

    @Override
    protected V generateAbsentValue(K key) {
        return null;
    }
}