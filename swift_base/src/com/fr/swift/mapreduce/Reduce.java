package com.fr.swift.mapreduce;

import java.util.Iterator;

/**
 * Created by Lyon on 18-1-1.
 */
public interface Reduce<K_IN, V_IN, K_OUT, V_OUT> {

    void reduce(K_IN key, Iterator<V_IN> in, OutCollector<K_OUT, V_OUT> out);
}
