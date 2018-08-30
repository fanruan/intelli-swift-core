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
package com.fr.swift.jdbc.rpc.pool.impl;

import com.fr.swift.jdbc.rpc.pool.GenericKeyedObjectPoolConfig;
import com.fr.swift.jdbc.rpc.pool.KeyedObjectPool;
import com.fr.swift.jdbc.rpc.pool.KeyedPooledObjectFactory;
import com.fr.swift.jdbc.rpc.pool.PoolUtils;
import com.fr.swift.jdbc.rpc.pool.PooledObject;
import com.fr.swift.jdbc.rpc.pool.PooledObjectState;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GenericKeyedObjectPool<K, T> extends BaseGenericObjectPool<T>
        implements KeyedObjectPool<K, T>, GenericKeyedObjectPoolMXBean<K> {

    // JMX specific attributes
    private static final String ONAME_BASE =
            "org.apache.commons.pool2:type=GenericKeyedObjectPool,name=";
    private final KeyedPooledObjectFactory<K, T> factory;
    private final boolean fairness;
    /**
     * My hash of sub-pools (ObjectQueue). The list of keys <b>must</b> be kept
     * in step with {@link #poolKeyList} using {@link #keyLock} to ensure any
     * changes to the list of current keys is made in a thread-safe manner.
     */
    private final Map<K, ObjectDeque<T>> poolMap =
            new ConcurrentHashMap<K, ObjectDeque<T>>();
    private final List<K> poolKeyList = new ArrayList<K>();
    private final ReadWriteLock keyLock = new ReentrantReadWriteLock(true);
    private final AtomicInteger numTotal = new AtomicInteger(0);
    private volatile int maxIdlePerKey =
            GenericKeyedObjectPoolConfig.DEFAULT_MAX_IDLE_PER_KEY;
    private volatile int minIdlePerKey =
            GenericKeyedObjectPoolConfig.DEFAULT_MIN_IDLE_PER_KEY;
    private volatile int maxTotalPerKey =
            GenericKeyedObjectPoolConfig.DEFAULT_MAX_TOTAL_PER_KEY;
    private Iterator<K> evictionKeyIterator = null;
    private K evictionKey = null;


    public GenericKeyedObjectPool(final KeyedPooledObjectFactory<K, T> factory) {
        this(factory, new GenericKeyedObjectPoolConfig());
    }


    /**
     * Create a new <code>GenericKeyedObjectPool</code> using a specific
     * configuration.
     *
     * @param factory the factory to be used to create entries
     * @param config  The configuration to use for this pool instance. The
     *                configuration is used by value. Subsequent changes to
     *                the configuration object will not be reflected in the
     *                pool.
     */
    public GenericKeyedObjectPool(final KeyedPooledObjectFactory<K, T> factory,
                                  final GenericKeyedObjectPoolConfig config) {

        super(config, ONAME_BASE, config.getJmxNamePrefix());

        if (factory == null) {
            jmxUnregister(); // tidy up
            throw new IllegalArgumentException("factory may not be null");
        }
        this.factory = factory;
        this.fairness = config.getFairness();

        setConfig(config);

        startEvictor(getTimeBetweenEvictionRunsMillis());
    }

    /**
     * Returns the limit on the number of object instances allocated by the pool
     * (checked out or idle), per key. When the limit is reached, the sub-pool
     * is said to be exhausted. A negative value indicates no limit.
     *
     * @return the limit on the number of active instances per key
     * @see #setMaxTotalPerKey
     */
    @Override
    public int getMaxTotalPerKey() {
        return maxTotalPerKey;
    }

    /**
     * Sets the limit on the number of object instances allocated by the pool
     * (checked out or idle), per key. When the limit is reached, the sub-pool
     * is said to be exhausted. A negative value indicates no limit.
     *
     * @param maxTotalPerKey the limit on the number of active instances per key
     * @see #getMaxTotalPerKey
     */
    public void setMaxTotalPerKey(final int maxTotalPerKey) {
        this.maxTotalPerKey = maxTotalPerKey;
    }

    /**
     * Returns the cap on the number of "idle" instances per key in the pool.
     * If maxIdlePerKey is set too low on heavily loaded systems it is possible
     * you will see objects being destroyed and almost immediately new objects
     * being created. This is a result of the active threads momentarily
     * returning objects faster than they are requesting them, causing the
     * number of idle objects to rise above maxIdlePerKey. The best value for
     * maxIdlePerKey for heavily loaded system will vary but the default is a
     * good starting point.
     *
     * @return the maximum number of "idle" instances that can be held in a
     * given keyed sub-pool or a negative value if there is no limit
     * @see #setMaxIdlePerKey
     */
    @Override
    public int getMaxIdlePerKey() {
        return maxIdlePerKey;
    }

    /**
     * Sets the cap on the number of "idle" instances per key in the pool.
     * If maxIdlePerKey is set too low on heavily loaded systems it is possible
     * you will see objects being destroyed and almost immediately new objects
     * being created. This is a result of the active threads momentarily
     * returning objects faster than they are requesting them, causing the
     * number of idle objects to rise above maxIdlePerKey. The best value for
     * maxIdlePerKey for heavily loaded system will vary but the default is a
     * good starting point.
     *
     * @param maxIdlePerKey the maximum number of "idle" instances that can be
     *                      held in a given keyed sub-pool. Use a negative value
     *                      for no limit
     * @see #getMaxIdlePerKey
     */
    public void setMaxIdlePerKey(final int maxIdlePerKey) {
        this.maxIdlePerKey = maxIdlePerKey;
    }

    /**
     * Returns the target for the minimum number of idle objects to maintain in
     * each of the keyed sub-pools. This setting only has an effect if it is
     * positive and {@link #getTimeBetweenEvictionRunsMillis()} is greater than
     * zero. If this is the case, an attempt is made to ensure that each
     * sub-pool has the required minimum number of instances during idle object
     * eviction runs.
     * <p>
     * If the configured value of minIdlePerKey is greater than the configured
     * value for maxIdlePerKey then the value of maxIdlePerKey will be used
     * instead.
     *
     * @return minimum size of the each keyed pool
     * @see #setTimeBetweenEvictionRunsMillis
     */
    @Override
    public int getMinIdlePerKey() {
        final int maxIdlePerKeySave = getMaxIdlePerKey();
        if (this.minIdlePerKey > maxIdlePerKeySave) {
            return maxIdlePerKeySave;
        }
        return minIdlePerKey;
    }

    /**
     * Sets the target for the minimum number of idle objects to maintain in
     * each of the keyed sub-pools. This setting only has an effect if it is
     * positive and {@link #getTimeBetweenEvictionRunsMillis()} is greater than
     * zero. If this is the case, an attempt is made to ensure that each
     * sub-pool has the required minimum number of instances during idle object
     * eviction runs.
     * <p>
     * If the configured value of minIdlePerKey is greater than the configured
     * value for maxIdlePerKey then the value of maxIdlePerKey will be used
     * instead.
     *
     * @param minIdlePerKey The minimum size of the each keyed pool
     * @see #getMinIdlePerKey
     * @see #getMaxIdlePerKey()
     * @see #setTimeBetweenEvictionRunsMillis
     */
    public void setMinIdlePerKey(final int minIdlePerKey) {
        this.minIdlePerKey = minIdlePerKey;
    }

    /**
     * Sets the configuration.
     *
     * @param conf the new configuration to use. This is used by value.
     * @see GenericKeyedObjectPoolConfig
     */
    public void setConfig(final GenericKeyedObjectPoolConfig conf) {
        setLifo(conf.getLifo());
        setMaxIdlePerKey(conf.getMaxIdlePerKey());
        setMaxTotalPerKey(conf.getMaxTotalPerKey());
        setMaxTotal(conf.getMaxTotal());
        setMinIdlePerKey(conf.getMinIdlePerKey());
        setMaxWaitMillis(conf.getMaxWaitMillis());
        setBlockWhenExhausted(conf.getBlockWhenExhausted());
        setTestOnCreate(conf.getTestOnCreate());
        setTestOnBorrow(conf.getTestOnBorrow());
        setTestOnReturn(conf.getTestOnReturn());
        setTestWhileIdle(conf.getTestWhileIdle());
        setNumTestsPerEvictionRun(conf.getNumTestsPerEvictionRun());
        setMinEvictableIdleTimeMillis(conf.getMinEvictableIdleTimeMillis());
        setSoftMinEvictableIdleTimeMillis(
                conf.getSoftMinEvictableIdleTimeMillis());
        setTimeBetweenEvictionRunsMillis(
                conf.getTimeBetweenEvictionRunsMillis());
        setEvictionPolicyClassName(conf.getEvictionPolicyClassName());
        setEvictorShutdownTimeoutMillis(conf.getEvictorShutdownTimeoutMillis());
    }

    /**
     * Obtain a reference to the factory used to create, destroy and validate
     * the objects used by this pool.
     *
     * @return the factory
     */
    public KeyedPooledObjectFactory<K, T> getFactory() {
        return factory;
    }

    /**
     * Equivalent to <code>{@link #borrowObject(Object, long) borrowObject}(key,
     * {@link #getMaxWaitMillis()})</code>.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public T borrowObject(final K key) throws Exception {
        return borrowObject(key, getMaxWaitMillis());
    }

    /**
     * Borrows an object from the sub-pool associated with the given key using
     * the specified waiting time which only applies if
     * {@link #getBlockWhenExhausted()} is true.
     * <p>
     * If there is one or more idle instances available in the sub-pool
     * associated with the given key, then an idle instance will be selected
     * based on the value of {@link #getLifo()}, activated and returned.  If
     * activation fails, or {@link #getTestOnBorrow() testOnBorrow} is set to
     * <code>true</code> and validation fails, the instance is destroyed and the
     * next available instance is examined.  This continues until either a valid
     * instance is returned or there are no more idle instances available.
     * <p>
     * If there are no idle instances available in the sub-pool associated with
     * the given key, behavior depends on the {@link #getMaxTotalPerKey()
     * maxTotalPerKey}, {@link #getMaxTotal() maxTotal}, and (if applicable)
     * {@link #getBlockWhenExhausted()} and the value passed in to the
     * <code>borrowMaxWaitMillis</code> parameter. If the number of instances checked
     * out from the sub-pool under the given key is less than
     * <code>maxTotalPerKey</code> and the total number of instances in
     * circulation (under all keys) is less than <code>maxTotal</code>, a new
     * instance is created, activated and (if applicable) validated and returned
     * to the caller. If validation fails, a <code>NoSuchElementException</code>
     * will be thrown.
     * <p>
     * If the associated sub-pool is exhausted (no available idle instances and
     * no capacity to create new ones), this method will either block
     * ({@link #getBlockWhenExhausted()} is true) or throw a
     * <code>NoSuchElementException</code>
     * ({@link #getBlockWhenExhausted()} is false).
     * The length of time that this method will block when
     * {@link #getBlockWhenExhausted()} is true is determined by the value
     * passed in to the <code>borrowMaxWait</code> parameter.
     * <p>
     * When <code>maxTotal</code> is set to a positive value and this method is
     * invoked when at the limit with no idle instances available under the requested
     * key, an attempt is made to create room by clearing the oldest 15% of the
     * elements from the keyed sub-pools.
     * <p>
     * When the pool is exhausted, multiple calling threads may be
     * simultaneously blocked waiting for instances to become available. A
     * "fairness" algorithm has been implemented to ensure that threads receive
     * available instances in request arrival order.
     *
     * @param key                 pool key
     * @param borrowMaxWaitMillis The time to wait in milliseconds for an object
     *                            to become available
     * @return object instance from the keyed pool
     * @throws NoSuchElementException if a keyed object instance cannot be
     *                                returned because the pool is exhausted.
     * @throws Exception              if a keyed object instance cannot be returned due to an
     *                                error
     */
    public T borrowObject(final K key, final long borrowMaxWaitMillis) throws Exception {
        assertOpen();

        PooledObject<T> p = null;

        // Get local copy of current config so it is consistent for entire
        // method execution
        final boolean blockWhenExhausted = getBlockWhenExhausted();

        boolean create;
        final long waitTime = System.currentTimeMillis();
        final ObjectDeque<T> objectDeque = register(key);

        try {
            while (p == null) {
                create = false;
                p = objectDeque.getIdleObjects().pollFirst();
                if (p == null) {
                    p = create(key);
                    if (p != null) {
                        create = true;
                    }
                }
                if (blockWhenExhausted) {
                    if (p == null) {
                        if (borrowMaxWaitMillis < 0) {
                            p = objectDeque.getIdleObjects().takeFirst();
                        } else {
                            p = objectDeque.getIdleObjects().pollFirst(
                                    borrowMaxWaitMillis, TimeUnit.MILLISECONDS);
                        }
                    }
                    if (p == null) {
                        throw new NoSuchElementException(
                                "Timeout waiting for idle object");
                    }
                } else {
                    if (p == null) {
                        throw new NoSuchElementException("Pool exhausted");
                    }
                }
                if (!p.allocate()) {
                    p = null;
                }

                if (p != null) {
                    try {
                        factory.activateObject(key, p);
                    } catch (final Exception e) {
                        try {
                            destroy(key, p, true);
                        } catch (final Exception e1) {
                            // Ignore - activation failure is more important
                        }
                        p = null;
                        if (create) {
                            final NoSuchElementException nsee = new NoSuchElementException(
                                    "Unable to activate object");
                            nsee.initCause(e);
                            throw nsee;
                        }
                    }
                    if (p != null && (getTestOnBorrow() || create && getTestOnCreate())) {
                        boolean validate = false;
                        Throwable validationThrowable = null;
                        try {
                            validate = factory.validateObject(key, p);
                        } catch (final Throwable t) {
                            PoolUtils.checkRethrow(t);
                            validationThrowable = t;
                        }
                        if (!validate) {
                            try {
                                destroy(key, p, true);
                                destroyedByBorrowValidationCount.incrementAndGet();
                            } catch (final Exception e) {
                                // Ignore - validation failure is more important
                            }
                            p = null;
                            if (create) {
                                final NoSuchElementException nsee = new NoSuchElementException(
                                        "Unable to validate object");
                                nsee.initCause(validationThrowable);
                                throw nsee;
                            }
                        }
                    }
                }
            }
        } finally {
            deregister(key);
        }

        updateStatsBorrow(p, System.currentTimeMillis() - waitTime);

        return p.getObject();
    }

    @Override
    public void returnObject(final K key, final T obj) {

        final ObjectDeque<T> objectDeque = poolMap.get(key);

        final PooledObject<T> p = objectDeque.getAllObjects().get(new IdentityWrapper<T>(obj));

        if (p == null) {
            throw new IllegalStateException(
                    "Returned object not currently part of this pool");
        }

        synchronized (p) {
            final PooledObjectState state = p.getState();
            if (state != PooledObjectState.ALLOCATED) {
                throw new IllegalStateException(
                        "Object has already been returned to this pool or is invalid");
            }
            p.markReturning(); // Keep from being marked abandoned (once GKOP does this)
        }

        final long activeTime = p.getActiveTimeMillis();

        try {
            if (getTestOnReturn()) {
                if (!factory.validateObject(key, p)) {
                    try {
                        destroy(key, p, true);
                    } catch (final Exception e) {
                        swallowException(e);
                    }
                    if (objectDeque.idleObjects.hasTakeWaiters()) {
                        try {
                            addObject(key);
                        } catch (final Exception e) {
                            swallowException(e);
                        }
                    }
                    return;
                }
            }

            try {
                factory.passivateObject(key, p);
            } catch (final Exception e1) {
                swallowException(e1);
                try {
                    destroy(key, p, true);
                } catch (final Exception e) {
                    swallowException(e);
                }
                if (objectDeque.idleObjects.hasTakeWaiters()) {
                    try {
                        addObject(key);
                    } catch (final Exception e) {
                        swallowException(e);
                    }
                }
                return;
            }

            if (!p.deallocate()) {
                throw new IllegalStateException(
                        "Object has already been returned to this pool");
            }

            final int maxIdle = getMaxIdlePerKey();
            final LinkedBlockingDeque<PooledObject<T>> idleObjects =
                    objectDeque.getIdleObjects();

            if (isClosed() || maxIdle > -1 && maxIdle <= idleObjects.size()) {
                try {
                    destroy(key, p, true);
                } catch (final Exception e) {
                    swallowException(e);
                }
            } else {
                if (getLifo()) {
                    idleObjects.addFirst(p);
                } else {
                    idleObjects.addLast(p);
                }
                if (isClosed()) {
                    clear(key);
                }
            }
        } finally {
            if (hasBorrowWaiters()) {
                reuseCapacity();
            }
            updateStatsReturn(activeTime);
        }
    }

    @Override
    public void invalidateObject(final K key, final T obj) throws Exception {

        final ObjectDeque<T> objectDeque = poolMap.get(key);

        final PooledObject<T> p = objectDeque.getAllObjects().get(new IdentityWrapper<T>(obj));
        if (p == null) {
            throw new IllegalStateException(
                    "Object not currently part of this pool");
        }
        synchronized (p) {
            if (p.getState() != PooledObjectState.INVALID) {
                destroy(key, p, true);
            }
        }
        if (objectDeque.idleObjects.hasTakeWaiters()) {
            addObject(key);
        }
    }

    @Override
    public void clear() {
        final Iterator<K> iter = poolMap.keySet().iterator();

        while (iter.hasNext()) {
            clear(iter.next());
        }
    }

    @Override
    public void clear(final K key) {

        final ObjectDeque<T> objectDeque = register(key);

        try {
            final LinkedBlockingDeque<PooledObject<T>> idleObjects =
                    objectDeque.getIdleObjects();

            PooledObject<T> p = idleObjects.poll();

            while (p != null) {
                try {
                    destroy(key, p, true);
                } catch (final Exception e) {
                    swallowException(e);
                }
                p = idleObjects.poll();
            }
        } finally {
            deregister(key);
        }
    }

    @Override
    public int getNumActive() {
        return numTotal.get() - getNumIdle();
    }

    @Override
    public int getNumIdle() {
        final Iterator<ObjectDeque<T>> iter = poolMap.values().iterator();
        int result = 0;

        while (iter.hasNext()) {
            result += iter.next().getIdleObjects().size();
        }

        return result;
    }

    @Override
    public int getNumActive(final K key) {
        final ObjectDeque<T> objectDeque = poolMap.get(key);
        if (objectDeque != null) {
            return objectDeque.getAllObjects().size() -
                    objectDeque.getIdleObjects().size();
        }
        return 0;
    }

    @Override
    public int getNumIdle(final K key) {
        final ObjectDeque<T> objectDeque = poolMap.get(key);
        return objectDeque != null ? objectDeque.getIdleObjects().size() : 0;
    }

    @Override
    public void close() {
        if (isClosed()) {
            return;
        }

        synchronized (closeLock) {
            if (isClosed()) {
                return;
            }

            // Stop the evictor before the pool is closed since evict() calls
            // assertOpen()
            startEvictor(-1L);

            closed = true;
            // This clear removes any idle objects
            clear();

            jmxUnregister();

            // Release any threads that were waiting for an object
            final Iterator<ObjectDeque<T>> iter = poolMap.values().iterator();
            while (iter.hasNext()) {
                iter.next().getIdleObjects().interuptTakeWaiters();
            }
            // This clear cleans up the keys now any waiting threads have been
            // interrupted
            clear();
        }
    }

    public void clearOldest() {

        // build sorted map of idle objects
        final Map<PooledObject<T>, K> map = new TreeMap<PooledObject<T>, K>();

        for (final Entry<K, ObjectDeque<T>> entry : poolMap.entrySet()) {
            final K k = entry.getKey();
            final ObjectDeque<T> deque = entry.getValue();
            // Protect against possible NPE if key has been removed in another
            // thread. Not worth locking the keys while this loop completes.
            if (deque != null) {
                final LinkedBlockingDeque<PooledObject<T>> idleObjects =
                        deque.getIdleObjects();
                for (final PooledObject<T> p : idleObjects) {
                    // each item into the map using the PooledObject object as the
                    // key. It then gets sorted based on the idle time
                    map.put(p, k);
                }
            }
        }

        // Now iterate created map and kill the first 15% plus one to account
        // for zero
        int itemsToRemove = ((int) (map.size() * 0.15)) + 1;
        final Iterator<Entry<PooledObject<T>, K>> iter =
                map.entrySet().iterator();

        while (iter.hasNext() && itemsToRemove > 0) {
            final Entry<PooledObject<T>, K> entry = iter.next();
            // kind of backwards on naming.  In the map, each key is the
            // PooledObject because it has the ordering with the timestamp
            // value.  Each value that the key references is the key of the
            // list it belongs to.
            final K key = entry.getValue();
            final PooledObject<T> p = entry.getKey();
            // Assume the destruction succeeds
            boolean destroyed = true;
            try {
                destroyed = destroy(key, p, false);
            } catch (final Exception e) {
                swallowException(e);
            }
            if (destroyed) {
                itemsToRemove--;
            }
        }
    }

    private void reuseCapacity() {
        final int maxTotalPerKeySave = getMaxTotalPerKey();

        // Find the most loaded pool that could take a new instance
        int maxQueueLength = 0;
        LinkedBlockingDeque<PooledObject<T>> mostLoaded = null;
        K loadedKey = null;
        for (final Entry<K, ObjectDeque<T>> entry : poolMap.entrySet()) {
            final K k = entry.getKey();
            final ObjectDeque<T> deque = entry.getValue();
            if (deque != null) {
                final LinkedBlockingDeque<PooledObject<T>> pool = deque.getIdleObjects();
                final int queueLength = pool.getTakeQueueLength();
                if (getNumActive(k) < maxTotalPerKeySave && queueLength > maxQueueLength) {
                    maxQueueLength = queueLength;
                    mostLoaded = pool;
                    loadedKey = k;
                }
            }
        }

        // Attempt to add an instance to the most loaded pool
        if (mostLoaded != null) {
            register(loadedKey);
            try {
                final PooledObject<T> p = create(loadedKey);
                if (p != null) {
                    addIdleObject(loadedKey, p);
                }
            } catch (final Exception e) {
                swallowException(e);
            } finally {
                deregister(loadedKey);
            }
        }
    }

    private boolean hasBorrowWaiters() {
        for (final Entry<K, ObjectDeque<T>> entry : poolMap.entrySet()) {
            final ObjectDeque<T> deque = entry.getValue();
            if (deque != null) {
                final LinkedBlockingDeque<PooledObject<T>> pool =
                        deque.getIdleObjects();
                if (pool.hasTakeWaiters()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void evict() throws Exception {
        assertOpen();

        if (getNumIdle() == 0) {
            return;
        }

        PooledObject<T> underTest = null;
        final EvictionPolicy<T> evictionPolicy = getEvictionPolicy();

        synchronized (evictionLock) {
            final EvictionConfig evictionConfig = getEvictionConfig();

            final boolean testWhileIdle = getTestWhileIdle();

            for (int i = 0, m = getNumTests(); i < m; i++) {
                handlerEvictionIterator();
                if (evictionIterator == null) {
                    return;
                }
                final Deque<PooledObject<T>> idleObjects;
                try {
                    underTest = evictionIterator.next();
                    idleObjects = evictionIterator.getIdleObjects();
                } catch (final NoSuchElementException nsee) {
                    i--;
                    evictionIterator = null;
                    continue;
                }

                if (!underTest.startEvictionTest()) {
                    i--;
                    continue;
                }

                boolean evict;
                try {
                    evict = evictionPolicy.evict(evictionConfig, underTest,
                            poolMap.get(evictionKey).getIdleObjects().size());
                } catch (final Throwable t) {
                    PoolUtils.checkRethrow(t);
                    swallowException(new Exception(t));
                    evict = false;
                }

                if (evict) {
                    destroy(evictionKey, underTest, true);
                    destroyedByEvictorCount.incrementAndGet();
                } else {
                    if (testWhileIdle) {
                        testWhileIdle(underTest);
                    }
                    if (!underTest.endEvictionTest(idleObjects)) {
                        // TODO - May need to add code here once additional
                        // states are used
                    }
                }
            }
        }
    }

    private void handlerEvictionIterator() {
        if (evictionIterator == null || !evictionIterator.hasNext()) {
            if (evictionKeyIterator == null ||
                    !evictionKeyIterator.hasNext()) {
                final List<K> keyCopy = new ArrayList<K>();
                final Lock readLock = keyLock.readLock();
                readLock.lock();
                try {
                    keyCopy.addAll(poolKeyList);
                } finally {
                    readLock.unlock();
                }
                evictionKeyIterator = keyCopy.iterator();
            }
            while (evictionKeyIterator.hasNext()) {
                evictionKey = evictionKeyIterator.next();
                final ObjectDeque<T> objectDeque = poolMap.get(evictionKey);
                if (objectDeque == null) {
                    continue;
                }

                final Deque<PooledObject<T>> idleObjects = objectDeque.getIdleObjects();
                evictionIterator = new EvictionIterator(idleObjects);
                if (evictionIterator.hasNext()) {
                    break;
                }
                evictionIterator = null;
            }
        }
    }

    private void testWhileIdle(PooledObject<T> underTest) throws Exception {
        boolean active = false;
        try {
            factory.activateObject(evictionKey, underTest);
            active = true;
        } catch (final Exception e) {
            destroy(evictionKey, underTest, true);
            destroyedByEvictorCount.incrementAndGet();
        }
        if (active) {
            if (!factory.validateObject(evictionKey, underTest)) {
                destroy(evictionKey, underTest, true);
                destroyedByEvictorCount.incrementAndGet();
            } else {
                try {
                    factory.passivateObject(evictionKey, underTest);
                } catch (final Exception e) {
                    destroy(evictionKey, underTest, true);
                    destroyedByEvictorCount.incrementAndGet();
                }
            }
        }
    }

    private EvictionConfig getEvictionConfig() {
        return new EvictionConfig(
                getMinEvictableIdleTimeMillis(),
                getSoftMinEvictableIdleTimeMillis(),
                getMinIdlePerKey());
    }

    private PooledObject<T> create(final K key) throws Exception {
        int maxTotalPerKeySave = getMaxTotalPerKey();
        if (maxTotalPerKeySave < 0) {
            maxTotalPerKeySave = Integer.MAX_VALUE;
        }
        final int maxTotal = getMaxTotal();

        final ObjectDeque<T> objectDeque = poolMap.get(key);

        boolean loop = true;

        while (loop) {
            final int newNumTotal = numTotal.incrementAndGet();
            if (maxTotal > -1 && newNumTotal > maxTotal) {
                numTotal.decrementAndGet();
                if (getNumIdle() == 0) {
                    return null;
                }
                clearOldest();
            } else {
                loop = false;
            }
        }

        Boolean create = null;
        while (create == null) {
            synchronized (objectDeque.makeObjectCountLock) {
                final long newCreateCount = objectDeque.getCreateCount().incrementAndGet();
                if (newCreateCount > maxTotalPerKeySave) {
                    objectDeque.getCreateCount().decrementAndGet();
                    if (objectDeque.makeObjectCount == 0) {
                        create = Boolean.FALSE;
                    } else {
                        objectDeque.makeObjectCountLock.wait();
                    }
                } else {
                    objectDeque.makeObjectCount++;
                    create = Boolean.TRUE;
                }
            }
        }

        if (!create.booleanValue()) {
            numTotal.decrementAndGet();
            return null;
        }

        PooledObject<T> p = null;
        try {
            p = factory.makeObject(key);
        } catch (final Exception e) {
            numTotal.decrementAndGet();
            objectDeque.getCreateCount().decrementAndGet();
            throw e;
        } finally {
            synchronized (objectDeque.makeObjectCountLock) {
                objectDeque.makeObjectCount--;
                objectDeque.makeObjectCountLock.notifyAll();
            }
        }

        createdCount.incrementAndGet();
        objectDeque.getAllObjects().put(new IdentityWrapper<T>(p.getObject()), p);
        return p;
    }

    private boolean destroy(final K key, final PooledObject<T> toDestroy, final boolean always)
            throws Exception {

        final ObjectDeque<T> objectDeque = register(key);

        try {
            final boolean isIdle = objectDeque.getIdleObjects().remove(toDestroy);

            if (isIdle || always) {
                objectDeque.getAllObjects().remove(new IdentityWrapper<T>(toDestroy.getObject()));
                toDestroy.invalidate();

                try {
                    factory.destroyObject(key, toDestroy);
                } finally {
                    objectDeque.getCreateCount().decrementAndGet();
                    destroyedCount.incrementAndGet();
                    numTotal.decrementAndGet();
                }
                return true;
            }
            return false;
        } finally {
            deregister(key);
        }
    }

    private ObjectDeque<T> register(final K k) {
        Lock lock = keyLock.readLock();
        ObjectDeque<T> objectDeque = null;
        try {
            lock.lock();
            objectDeque = poolMap.get(k);
            if (objectDeque == null) {
                // Upgrade to write lock
                lock.unlock();
                lock = keyLock.writeLock();
                lock.lock();
                objectDeque = poolMap.get(k);
                if (objectDeque == null) {
                    objectDeque = new ObjectDeque<T>(fairness);
                    objectDeque.getNumInterested().incrementAndGet();
                    poolMap.put(k, objectDeque);
                    poolKeyList.add(k);
                } else {
                    objectDeque.getNumInterested().incrementAndGet();
                }
            } else {
                objectDeque.getNumInterested().incrementAndGet();
            }
        } finally {
            lock.unlock();
        }
        return objectDeque;
    }

    private void deregister(final K k) {
        ObjectDeque<T> objectDeque;

        objectDeque = poolMap.get(k);
        final long numInterested = objectDeque.getNumInterested().decrementAndGet();
        if (numInterested == 0 && objectDeque.getCreateCount().get() == 0) {
            // Potential to remove key
            final Lock writeLock = keyLock.writeLock();
            writeLock.lock();
            try {
                if (objectDeque.getCreateCount().get() == 0 &&
                        objectDeque.getNumInterested().get() == 0) {
                    poolMap.remove(k);
                    poolKeyList.remove(k);
                }
            } finally {
                writeLock.unlock();
            }
        }
    }

    @Override
    void ensureMinIdle() throws Exception {
        final int minIdlePerKeySave = getMinIdlePerKey();
        if (minIdlePerKeySave < 1) {
            return;
        }

        for (final K k : poolMap.keySet()) {
            ensureMinIdle(k);
        }
    }

    private void ensureMinIdle(final K key) throws Exception {
        ObjectDeque<T> objectDeque = poolMap.get(key);
        final int deficit = calculateDeficit(objectDeque);

        for (int i = 0; i < deficit && calculateDeficit(objectDeque) > 0; i++) {
            addObject(key);
            if (objectDeque == null) {
                objectDeque = poolMap.get(key);
            }
        }
    }

    @Override
    public void addObject(final K key) throws Exception {
        assertOpen();
        register(key);
        try {
            final PooledObject<T> p = create(key);
            addIdleObject(key, p);
        } finally {
            deregister(key);
        }
    }

    /**
     * Add an object to the set of idle objects for a given key.
     *
     * @param key The key to associate with the idle object
     * @param p   The wrapped object to add.
     * @throws Exception If the associated factory fails to passivate the object
     */
    private void addIdleObject(final K key, final PooledObject<T> p) throws Exception {

        if (p != null) {
            factory.passivateObject(key, p);
            final LinkedBlockingDeque<PooledObject<T>> idleObjects =
                    poolMap.get(key).getIdleObjects();
            if (getLifo()) {
                idleObjects.addFirst(p);
            } else {
                idleObjects.addLast(p);
            }
        }
    }

    /**
     * Registers a key for pool control and ensures that
     * {@link #getMinIdlePerKey()} idle instances are created.
     *
     * @param key - The key to register for pool control.
     * @throws Exception If the associated factory throws an exception
     */
    public void preparePool(final K key) throws Exception {
        final int minIdlePerKeySave = getMinIdlePerKey();
        if (minIdlePerKeySave < 1) {
            return;
        }
        ensureMinIdle(key);
    }


    //--- internal attributes --------------------------------------------------

    /**
     * Calculate the number of objects to test in a run of the idle object
     * evictor.
     *
     * @return The number of objects to test for validity
     */
    private int getNumTests() {
        final int totalIdle = getNumIdle();
        final int numTests = getNumTestsPerEvictionRun();
        if (numTests >= 0) {
            return Math.min(numTests, totalIdle);
        }
        return (int) (Math.ceil(totalIdle / Math.abs((double) numTests)));
    }

    /**
     * Calculate the number of objects that need to be created to attempt to
     * maintain the minimum number of idle objects while not exceeded the limits
     * on the maximum number of objects either per key or totally.
     *
     * @param objectDeque The set of objects to check
     * @return The number of new objects to create
     */
    private int calculateDeficit(final ObjectDeque<T> objectDeque) {

        if (objectDeque == null) {
            return getMinIdlePerKey();
        }

        // Used more than once so keep a local copy so the value is consistent
        final int maxTotal = getMaxTotal();
        final int maxTotalPerKeySave = getMaxTotalPerKey();

        int objectDefecit = 0;

        // Calculate no of objects needed to be created, in order to have
        // the number of pooled objects < maxTotalPerKey();
        objectDefecit = getMinIdlePerKey() - objectDeque.getIdleObjects().size();
        if (maxTotalPerKeySave > 0) {
            final int growLimit = Math.max(0,
                    maxTotalPerKeySave - objectDeque.getIdleObjects().size());
            objectDefecit = Math.min(objectDefecit, growLimit);
        }

        // Take the maxTotal limit into account
        if (maxTotal > 0) {
            final int growLimit = Math.max(0, maxTotal - getNumActive() - getNumIdle());
            objectDefecit = Math.min(objectDefecit, growLimit);
        }

        return objectDefecit;
    }

    @Override
    public Map<String, Integer> getNumActivePerKey() {
        final HashMap<String, Integer> result = new HashMap<String, Integer>();

        final Iterator<Entry<K, ObjectDeque<T>>> iter = poolMap.entrySet().iterator();
        while (iter.hasNext()) {
            final Entry<K, ObjectDeque<T>> entry = iter.next();
            if (entry != null) {
                final K key = entry.getKey();
                final ObjectDeque<T> objectDequeue = entry.getValue();
                if (key != null && objectDequeue != null) {
                    result.put(key.toString(),
                            objectDequeue.getAllObjects().size() -
                                    objectDequeue.getIdleObjects().size());
                }
            }
        }
        return result;
    }

    @Override
    public int getNumWaiters() {
        int result = 0;

        if (getBlockWhenExhausted()) {
            final Iterator<ObjectDeque<T>> iter = poolMap.values().iterator();

            while (iter.hasNext()) {
                // Assume no overflow
                result += iter.next().getIdleObjects().getTakeQueueLength();
            }
        }

        return result;
    }

    @Override
    public Map<String, Integer> getNumWaitersByKey() {
        final Map<String, Integer> result = new HashMap<String, Integer>();

        for (final Entry<K, ObjectDeque<T>> entry : poolMap.entrySet()) {
            final K k = entry.getKey();
            final ObjectDeque<T> deque = entry.getValue();
            if (deque != null) {
                if (getBlockWhenExhausted()) {
                    result.put(k.toString(),
                            deque.getIdleObjects().getTakeQueueLength());
                } else {
                    result.put(k.toString(), 0);
                }
            }
        }
        return result;
    }

    @Override
    public Map<String, List<DefaultPooledObjectInfo>> listAllObjects() {
        final Map<String, List<DefaultPooledObjectInfo>> result =
                new HashMap<String, List<DefaultPooledObjectInfo>>();

        for (final Entry<K, ObjectDeque<T>> entry : poolMap.entrySet()) {
            final K k = entry.getKey();
            final ObjectDeque<T> deque = entry.getValue();
            if (deque != null) {
                final List<DefaultPooledObjectInfo> list =
                        new ArrayList<DefaultPooledObjectInfo>();
                result.put(k.toString(), list);
                for (final PooledObject<T> p : deque.getAllObjects().values()) {
                    list.add(new DefaultPooledObjectInfo(p));
                }
            }
        }
        return result;
    }

    @Override
    protected void toStringAppendFields(final StringBuilder builder) {
        super.toStringAppendFields(builder);
        builder.append(", maxIdlePerKey=");
        builder.append(maxIdlePerKey);
        builder.append(", minIdlePerKey=");
        builder.append(minIdlePerKey);
        builder.append(", maxTotalPerKey=");
        builder.append(maxTotalPerKey);
        builder.append(", factory=");
        builder.append(factory);
        builder.append(", fairness=");
        builder.append(fairness);
        builder.append(", poolMap=");
        builder.append(poolMap);
        builder.append(", poolKeyList=");
        builder.append(poolKeyList);
        builder.append(", keyLock=");
        builder.append(keyLock);
        builder.append(", numTotal=");
        builder.append(numTotal);
        builder.append(", evictionKeyIterator=");
        builder.append(evictionKeyIterator);
        builder.append(", evictionKey=");
        builder.append(evictionKey);
    }

    /**
     * Maintains information on the per key queue for a given key.
     */
    private class ObjectDeque<S> {

        private final LinkedBlockingDeque<PooledObject<S>> idleObjects;

        private final AtomicInteger createCount = new AtomicInteger(0);
        private final Object makeObjectCountLock = new Object();
        private final Map<IdentityWrapper<S>, PooledObject<S>> allObjects =
                new ConcurrentHashMap<IdentityWrapper<S>, PooledObject<S>>();
        private final AtomicLong numInterested = new AtomicLong(0);
        private long makeObjectCount = 0;

        /**
         * Create a new ObjecDeque with the given fairness policy.
         *
         * @param fairness true means client threads waiting to borrow / return instances
         *                 will be served as if waiting in a FIFO queue.
         */
        public ObjectDeque(final boolean fairness) {
            idleObjects = new LinkedBlockingDeque<PooledObject<S>>(fairness);
        }

        /**
         * Obtain the idle objects for the current key.
         *
         * @return The idle objects
         */
        public LinkedBlockingDeque<PooledObject<S>> getIdleObjects() {
            return idleObjects;
        }

        /**
         * Obtain the count of the number of objects created for the current
         * key.
         *
         * @return The number of objects created for this key
         */
        public AtomicInteger getCreateCount() {
            return createCount;
        }

        /**
         * Obtain the number of threads with an interest registered in this key.
         *
         * @return The number of threads with a registered interest in this key
         */
        public AtomicLong getNumInterested() {
            return numInterested;
        }

        /**
         * Obtain all the objects for the current key.
         *
         * @return All the objects
         */
        public Map<IdentityWrapper<S>, PooledObject<S>> getAllObjects() {
            return allObjects;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("ObjectDeque [idleObjects=");
            builder.append(idleObjects);
            builder.append(", createCount=");
            builder.append(createCount);
            builder.append(", allObjects=");
            builder.append(allObjects);
            builder.append(", numInterested=");
            builder.append(numInterested);
            builder.append("]");
            return builder.toString();
        }

    }
}
