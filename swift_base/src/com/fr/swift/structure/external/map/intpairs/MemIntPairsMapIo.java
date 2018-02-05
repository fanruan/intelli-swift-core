package com.fr.swift.structure.external.map.intpairs;

import com.fr.swift.structure.Pair;
import com.fr.swift.structure.external.map.ExternalMapIO;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * @author anchore
 * @date 2018/1/5
 */
class MemIntPairsMapIo<K> implements ExternalMapIO<K, List<Pair<Integer, Integer>>> {
    private Iterator<Map.Entry<K, List<Pair<Integer, Integer>>>> itr;

    MemIntPairsMapIo(SortedMap<K, List<Pair<Integer, Integer>>> map) {
        itr = map.entrySet().iterator();
    }

    @Override
    public void write(K key, List<Pair<Integer, Integer>> value) {
    }

    @Override
    public Pair<K, List<Pair<Integer, Integer>>> read() {
        if (!itr.hasNext()) {
            return null;
        }
        Map.Entry<K, List<Pair<Integer, Integer>>> entry = itr.next();
        return new Pair<K, List<Pair<Integer, Integer>>>(entry.getKey(), entry.getValue());
    }

    @Override
    public void close() {
    }

    @Override
    public void setSize(int size) {
    }
}
