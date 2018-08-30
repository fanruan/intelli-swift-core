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

import java.util.NoSuchElementException;

public interface KeyedObjectPool<K, V> {
    V borrowObject(K key) throws Exception, NoSuchElementException, IllegalStateException;

    void returnObject(K key, V obj) throws Exception;

    void invalidateObject(K key, V obj) throws Exception;

    void addObject(K key) throws Exception, IllegalStateException,
            UnsupportedOperationException;

    int getNumIdle(K key);

    int getNumActive(K key);

    int getNumIdle();

    int getNumActive();

    void clear() throws Exception, UnsupportedOperationException;

    void clear(K key) throws Exception, UnsupportedOperationException;

    void close();
}
