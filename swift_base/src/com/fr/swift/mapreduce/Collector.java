package com.fr.swift.mapreduce;

/**
 * Created by Lyon on 2018/1/10.
 */
public interface Collector<K, V> extends InCollector<K, V>, OutCollector<K, V> {
}
