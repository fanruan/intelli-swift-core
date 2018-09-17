package com.fr.swift.structure.lru;

public class LRUHashMap<K, V> extends FIFOHashMap<K, V> {

    public LRUHashMap() {
        super();
    }

    public LRUHashMap(int max_entries) {
        super(max_entries);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(Object key) {
        V v = super.get(key);
        if (v != null) {
            put((K) key, v);
        }
        return v;
    }
}