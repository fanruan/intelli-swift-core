package com.finebi.cube.engine.map.map2;


import com.finebi.cube.engine.map.ExternalMapIO;
import com.fr.stable.collections.array.IntArray;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Connery on 2015/11/30.
 */
public class MemIntArrayExternalMapIO<K> implements ExternalMapIO<K, IntArray> {
    private TreeMap<K, IntArray> currentContainer;
    private Iterator<K> iterator;

    public MemIntArrayExternalMapIO(TreeMap<K, IntArray> currentContainer) {
        this.currentContainer = new TreeMap<K, IntArray>(currentContainer);
        this.iterator = currentContainer.keySet().iterator();
    }

    @Override
    public void write(K key, IntArray value) {

    }

    @Override
    public Map<K, IntArray> read() throws FileNotFoundException {
        TreeMap<K, IntArray> result = new TreeMap<K, IntArray>();
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