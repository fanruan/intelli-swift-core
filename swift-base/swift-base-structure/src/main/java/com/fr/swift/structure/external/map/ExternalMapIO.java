package com.fr.swift.structure.external.map;

import com.fr.swift.structure.Pair;
import com.fr.swift.util.Closable;

import java.io.FileNotFoundException;

/**
 * @author FineSoft
 * @date 2015/7/15
 */
public interface ExternalMapIO<K, V> extends Closable {
    void write(K key, V value);

    Pair<K, V> read() throws FileNotFoundException;

    @Override
    void close();

    void setSize(int size);
}