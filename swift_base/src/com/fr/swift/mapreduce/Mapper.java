package com.fr.swift.mapreduce;

/**
 * Created by Lyon on 18-1-1.
 */
public interface Mapper<K_IN, V_IN, K_OUT, V_OUT> {

    void map(KeyValue<K_IN, V_IN> keyValue, OutCollector<K_OUT, V_OUT> outCollector);
}
