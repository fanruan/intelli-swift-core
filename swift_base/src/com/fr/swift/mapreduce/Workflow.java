package com.fr.swift.mapreduce;

/**
 * Created by Lyon on 18-1-1.
 */
public interface Workflow<K, V> {

    InCollector<K, V> run();
}
