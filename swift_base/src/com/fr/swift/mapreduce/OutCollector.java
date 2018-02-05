package com.fr.swift.mapreduce;

/**
 * Created by Lyon on 18-1-1.
 */
public interface OutCollector<K, V> {

    void collect(KeyValue<K, V> keyValue);
}
