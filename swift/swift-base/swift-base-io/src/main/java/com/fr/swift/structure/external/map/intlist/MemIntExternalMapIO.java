package com.fr.swift.structure.external.map.intlist;

import com.fr.swift.structure.Pair;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.external.map.ExternalMapIO;

import java.util.Iterator;
import java.util.TreeMap;

/**
 * Created by Connery on 2015/11/30.
 */
class MemIntExternalMapIO<K> implements ExternalMapIO<K, IntList> {
    private TreeMap<K, IntList> currentContainer;
    private Iterator<K> iterator;

    public MemIntExternalMapIO(TreeMap<K, IntList> currentContainer) {
        this.currentContainer = new TreeMap<K, IntList>(currentContainer);
        this.iterator = currentContainer.keySet().iterator();
    }

    @Override
    public void write(K key, IntList value) {

    }

    @Override
    public Pair<K, IntList> read() {
        if (iterator.hasNext()) {
            K key = iterator.next();
            return Pair.of(key, currentContainer.get(key));
        } else {
            return null;
        }

    }

    @Override
    public void close() {

    }

    @Override
    public void setSize(int size) {

    }
}