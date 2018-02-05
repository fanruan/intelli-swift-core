package com.fr.swift.mapreduce.impl;

import com.fr.swift.mapreduce.Collector;
import com.fr.swift.mapreduce.KeyValue;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Lyon on 18-1-1.
 */
public class MapCollector<K, V> implements Collector<K, V> {

    private ConcurrentHashMap<K, KeyValue<K, V>> collector = new ConcurrentHashMap<K, KeyValue<K, V>>();

    public V get(K key) {
        return collector.get(key).getValue();
    }

    @Override
    public void collect(KeyValue<K, V> keyValue) {
        if (collector.contains(keyValue.getKey())) {
            return;
        }
        collector.put(keyValue.getKey(), keyValue);
    }

    @Override
    public Iterator<KeyValue<K, V>> iterator() {
        return collector.values().iterator();
    }
}
