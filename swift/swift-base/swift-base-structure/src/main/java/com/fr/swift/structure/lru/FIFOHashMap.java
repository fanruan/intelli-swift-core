package com.fr.swift.structure.lru;

import java.util.LinkedHashMap;
import java.util.Map;

public class FIFOHashMap<K, V> extends LinkedHashMap<K, V> {

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