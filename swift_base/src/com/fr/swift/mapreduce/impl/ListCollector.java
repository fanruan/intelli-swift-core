package com.fr.swift.mapreduce.impl;

import com.fr.swift.mapreduce.Collector;
import com.fr.swift.mapreduce.KeyValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 18-1-1.
 */
public class ListCollector<K, V> implements Collector<K, V> {

    private List<KeyValue<K, V>> collector = new ArrayList<KeyValue<K, V>>();

    @Override
    public void collect(KeyValue<K, V> keyValue) {
        collector.add(keyValue);
    }

    @Override
    public Iterator<KeyValue<K, V>> iterator() {
        return collector.iterator();
    }
}
