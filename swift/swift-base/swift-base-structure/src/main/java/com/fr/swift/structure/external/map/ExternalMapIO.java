package com.fr.swift.structure.external.map;

import com.fr.swift.structure.Pair;

import java.io.FileNotFoundException;

/**
 * @author FineSoft
 * @date 2015/7/15
 */
public interface ExternalMapIO<K, V> {
    void write(K key, V value);

    Pair<K, V> read() throws FileNotFoundException;

    void close();

    void setSize(int size);
}