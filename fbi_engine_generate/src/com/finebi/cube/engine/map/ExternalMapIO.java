package com.finebi.cube.engine.map;

import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Created by FineSoft on 2015/7/15.
 */
public interface ExternalMapIO<K, V> {
    void write(K key, V value);

    Map<K, V> read()throws FileNotFoundException;

    void close();

    void setSize(Integer size);
}