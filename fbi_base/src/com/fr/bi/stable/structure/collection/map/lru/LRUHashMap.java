/**
 *
 */
package com.fr.bi.stable.structure.collection.map.lru;


public class LRUHashMap<K, V> extends FIFOHashMap<K, V> {
    /**
     *
     */
    private static final long serialVersionUID = -4255648430483158838L;

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