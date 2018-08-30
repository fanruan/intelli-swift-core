package com.fr.swift.jdbc.rpc.pool;

public interface KeyedPooledObjectFactory<K, V> {
    PooledObject<V> makeObject(K key) throws Exception;

    void destroyObject(K key, PooledObject<V> p) throws Exception;

    boolean validateObject(K key, PooledObject<V> p);

    void activateObject(K key, PooledObject<V> p) throws Exception;

    void passivateObject(K key, PooledObject<V> p) throws Exception;
}

