package com.finebi.base.container;

import java.util.Map;
import java.util.TreeMap;

/**
 * This class created on 2017/9/29.
 *
 * @author Each.Zhang
 */
public class TreeMapContainer<K, V> extends MapContainer<K, V> {
    @Override
    protected Map<K, V> initContainer() {
        return new TreeMap<K, V>();
    }

    @Override
    protected V generateAbsentValue(K key) {
        return null;
    }
}
