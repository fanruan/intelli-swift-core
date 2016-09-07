package com.finebi.cube.engine.map.map2;


import com.finebi.cube.engine.map.ExternalMapIO;
import com.fr.bi.stable.structure.collection.list.IntArrayList;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Connery on 2015/11/30.
 */
public class MemIntArrayExternalMapIO<K> implements ExternalMapIO<K, IntArrayList> {
    private TreeMap<K, IntArrayList> currentContainer;
    private Iterator<K> iterator;

    public MemIntArrayExternalMapIO(TreeMap<K, IntArrayList> currentContainer) {
        this.currentContainer = new TreeMap<K, IntArrayList>(currentContainer);
        this.iterator = currentContainer.keySet().iterator();
    }

    @Override
    public void write(K key, IntArrayList value) {

    }

    @Override
    public Map<K, IntArrayList> read() throws FileNotFoundException {
        TreeMap<K, IntArrayList> result = new TreeMap<K, IntArrayList>();
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