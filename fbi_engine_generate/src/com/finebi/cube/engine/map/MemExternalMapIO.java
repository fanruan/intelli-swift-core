package com.finebi.cube.engine.map;


import com.fr.bi.stable.structure.collection.list.IntList;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Connery on 2015/11/30.
 */
public class MemExternalMapIO<K> implements ExternalMapIO<K, IntList> {
    private TreeMap<K, IntList> currentContainer;
    private Iterator<K> iterator;

    public MemExternalMapIO(TreeMap<K, IntList> currentContainer) {
        this.currentContainer = new TreeMap<K, IntList>(currentContainer);
        this.iterator = currentContainer.keySet().iterator();
    }

    @Override
    public void write(K key, IntList value) {

    }

    @Override
    public Map<K, IntList> read() throws FileNotFoundException {
        TreeMap<K, IntList> result = new TreeMap<K, IntList>();
        if (iterator.hasNext()) {
            K key = iterator.next();
            result.put(key, currentContainer.get(key));
            return result;
        } else {
            return null;
        }

    }

    @Override
    public void close() {

    }

    @Override
    public void setSize(Integer size) {

    }
}