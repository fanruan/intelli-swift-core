/**
 *
 */
package com.fr.bi.stable.structure.collection.map.lru;

import java.util.LinkedHashMap;
import java.util.Map;


public class FIFOHashMap<K, V> extends LinkedHashMap<K, V> {

    /**
     *
     */
    private static final long serialVersionUID = -6833978343697380558L;

    private int max_entries = 16;

    public FIFOHashMap() {
        super();
    }

    public FIFOHashMap(int max_entries) {
        super();
        this.max_entries = max_entries;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        if (size() > max_entries) {
            return true;
        }
        return false;
    }

}