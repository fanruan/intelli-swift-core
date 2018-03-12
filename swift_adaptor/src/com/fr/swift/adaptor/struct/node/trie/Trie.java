package com.fr.swift.adaptor.struct.node.trie;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Lyon on 2018/3/4.
 */
public interface Trie<RAW_KEY, K, V> {

    void insert(RAW_KEY key, V value);

    void delete(RAW_KEY key);

    Trie<RAW_KEY, K, V> parent();

    Trie<RAW_KEY, K, V> get(RAW_KEY key);

    V getValue();

    void setValue(V value);

    K getKey();

    boolean containsKey(RAW_KEY key);

    int deep();

    Iterator<Map.Entry<K, Trie<RAW_KEY, K, V>>> iterator();
}
