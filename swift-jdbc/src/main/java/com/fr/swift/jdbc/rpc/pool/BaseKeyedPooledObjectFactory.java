/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fr.swift.jdbc.rpc.pool;

public abstract class BaseKeyedPooledObjectFactory<K, V>
        implements KeyedPooledObjectFactory<K, V> {

    public abstract V create(K key)
            throws Exception;

    public abstract PooledObject<V> wrap(V value);

    @Override
    public PooledObject<V> makeObject(final K key) throws Exception {
        return wrap(create(key));
    }

    @Override
    public void destroyObject(final K key, final PooledObject<V> p)
            throws Exception {
    }

    @Override
    public boolean validateObject(final K key, final PooledObject<V> p) {
        return true;
    }

    @Override
    public void activateObject(final K key, final PooledObject<V> p)
            throws Exception {
    }

    @Override
    public void passivateObject(final K key, final PooledObject<V> p)
            throws Exception {
    }
}
