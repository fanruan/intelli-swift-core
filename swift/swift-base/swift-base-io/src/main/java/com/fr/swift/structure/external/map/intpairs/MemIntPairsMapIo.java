package com.fr.swift.structure.external.map.intpairs;

import com.fr.swift.structure.IntPair;
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
class MemIntPairsMapIo<K> implements ExternalMapIO<K, List<IntPair>> {
    private Iterator<Map.Entry<K, List<IntPair>>> itr;

    MemIntPairsMapIo(SortedMap<K, List<IntPair>> map) {
        itr = map.entrySet().iterator();
    }

    @Override
    public void write(K key, List<IntPair> value) {
    }

    @Override
    public Pair<K, List<IntPair>> read() {
        if (!itr.hasNext()) {
            return null;
        }
        Map.Entry<K, List<IntPair>> entry = itr.next();
        return Pair.of(entry.getKey(), entry.getValue());
    }

    @Override
    public void close() {
    }

    @Override
    public void setSize(int size) {
    }
}
