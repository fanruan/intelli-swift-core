package com.fr.swift.adaptor.struct.node.trie;

/**
 * Created by Lyon on 2018/3/4.
 */
public interface Trie<K, V> {

    Trie<K, V> insert(K key, V value);

    Trie<K, V> delete(K key);

    Trie<K, V> getParent();

    Trie<K, V> get(K key);

    V getData();

    boolean containsKey(K key);
}
