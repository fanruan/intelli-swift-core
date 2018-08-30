package com.fr.swift.jdbc.rpc.pool;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public final class PoolUtils {

    public PoolUtils() {
    }

    public static void checkRethrow(final Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath) t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError) t;
        }
    }

    public static <K, V> TimerTask checkMinIdle(
            final KeyedObjectPool<K, V> keyedPool, final K key,
            final int minIdle, final long period)
            throws IllegalArgumentException {
        if (keyedPool == null) {
            throw new IllegalArgumentException("keyedPool must not be null.");
        }
        if (key == null) {
            throw new IllegalArgumentException("key must not be null.");
        }
        if (minIdle < 0) {
            throw new IllegalArgumentException("minIdle must be non-negative.");
        }
        final TimerTask task = new KeyedObjectPoolMinIdleTimerTask<K, V>(
                keyedPool, key, minIdle);
        getMinIdleTimer().schedule(task, 0L, period);
        return task;
    }

    public static <K, V> Map<K, TimerTask> checkMinIdle(
            final KeyedObjectPool<K, V> keyedPool, final Collection<K> keys,
            final int minIdle, final long period)
            throws IllegalArgumentException {
        if (keys == null) {
            throw new IllegalArgumentException("keys must not be null.");
        }
        final Map<K, TimerTask> tasks = new HashMap<K, TimerTask>(keys.size());
        final Iterator<K> iter = keys.iterator();
        while (iter.hasNext()) {
            final K key = iter.next();
            final TimerTask task = checkMinIdle(keyedPool, key, minIdle, period);
            tasks.put(key, task);
        }
        return tasks;
    }

    public static <K, V> void prefill(final KeyedObjectPool<K, V> keyedPool,
                                      final K key, final int count) throws Exception,
            IllegalArgumentException {
        if (keyedPool == null) {
            throw new IllegalArgumentException("keyedPool must not be null.");
        }
        if (key == null) {
            throw new IllegalArgumentException("key must not be null.");
        }
        for (int i = 0; i < count; i++) {
            keyedPool.addObject(key);
        }
    }

    public static <K, V> void prefill(final KeyedObjectPool<K, V> keyedPool,
                                      final Collection<K> keys, final int count) throws Exception,
            IllegalArgumentException {
        if (keys == null) {
            throw new IllegalArgumentException("keys must not be null.");
        }
        final Iterator<K> iter = keys.iterator();
        while (iter.hasNext()) {
            prefill(keyedPool, iter.next(), count);
        }
    }

    public static <K, V> KeyedObjectPool<K, V> synchronizedPool(
            final KeyedObjectPool<K, V> keyedPool) {
        return new SynchronizedKeyedObjectPool<K, V>(keyedPool);
    }

    public static <K, V> KeyedPooledObjectFactory<K, V> synchronizedKeyedPooledFactory(
            final KeyedPooledObjectFactory<K, V> keyedFactory) {
        return new SynchronizedKeyedPooledObjectFactory<K, V>(keyedFactory);
    }

    public static <K, V> KeyedObjectPool<K, V> erodingPool(
            final KeyedObjectPool<K, V> keyedPool) {
        return erodingPool(keyedPool, 1f);
    }

    public static <K, V> KeyedObjectPool<K, V> erodingPool(
            final KeyedObjectPool<K, V> keyedPool, final float factor) {
        return erodingPool(keyedPool, factor, false);
    }

    public static <K, V> KeyedObjectPool<K, V> erodingPool(
            final KeyedObjectPool<K, V> keyedPool, final float factor,
            final boolean perKey) {
        if (keyedPool == null) {
            throw new IllegalArgumentException("keyedPool must not be null.");
        }
        if (factor <= 0f) {
            throw new IllegalArgumentException("factor must be positive.");
        }
        if (perKey) {
            return new ErodingPerKeyKeyedObjectPool<K, V>(keyedPool, factor);
        }
        return new ErodingKeyedObjectPool<K, V>(keyedPool, factor);
    }

    private static Timer getMinIdleTimer() {
        return TimerHolder.MIN_IDLE_TIMER;
    }

    static class TimerHolder {
        static final Timer MIN_IDLE_TIMER = new Timer(true);
    }

    private static final class KeyedObjectPoolMinIdleTimerTask<K, V> extends
            TimerTask {
        /**
         * Minimum number of idle instances. Not the same as pool.getMinIdle().
         */
        private final int minIdle;

        /**
         * Key to ensure minIdle for
         */
        private final K key;

        /**
         * Keyed object pool
         */
        private final KeyedObjectPool<K, V> keyedPool;

        /**
         * Create a new KeyedObjecPoolMinIdleTimerTask.
         *
         * @param keyedPool keyed object pool
         * @param key       key to ensure minimum number of idle instances
         * @param minIdle   minimum number of idle instances
         * @throws IllegalArgumentException if the key is null
         */
        KeyedObjectPoolMinIdleTimerTask(final KeyedObjectPool<K, V> keyedPool,
                                        final K key, final int minIdle) throws IllegalArgumentException {
            if (keyedPool == null) {
                throw new IllegalArgumentException(
                        "keyedPool must not be null.");
            }
            this.keyedPool = keyedPool;
            this.key = key;
            this.minIdle = minIdle;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            boolean success = false;
            try {
                if (keyedPool.getNumIdle(key) < minIdle) {
                    keyedPool.addObject(key);
                }
                success = true;

            } catch (final Exception e) {
                cancel();

            } finally {
                // detect other types of Throwable and cancel this Timer
                if (!success) {
                    cancel();
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("KeyedObjectPoolMinIdleTimerTask");
            sb.append("{minIdle=").append(minIdle);
            sb.append(", key=").append(key);
            sb.append(", keyedPool=").append(keyedPool);
            sb.append('}');
            return sb.toString();
        }
    }

    private static final class SynchronizedKeyedObjectPool<K, V> implements
            KeyedObjectPool<K, V> {

        /**
         * Object whose monitor is used to synchronize methods on the wrapped
         * pool.
         */
        private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        /**
         * Underlying object pool
         */
        private final KeyedObjectPool<K, V> keyedPool;

        /**
         * Create a new SynchronizedKeyedObjectPool wrapping the given pool
         *
         * @param keyedPool KeyedObjectPool to wrap
         * @throws IllegalArgumentException if keyedPool is null
         */
        SynchronizedKeyedObjectPool(final KeyedObjectPool<K, V> keyedPool)
                throws IllegalArgumentException {
            if (keyedPool == null) {
                throw new IllegalArgumentException(
                        "keyedPool must not be null.");
            }
            this.keyedPool = keyedPool;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V borrowObject(final K key) throws Exception,
                NoSuchElementException, IllegalStateException {
            final WriteLock writeLock = readWriteLock.writeLock();
            writeLock.lock();
            try {
                return keyedPool.borrowObject(key);
            } finally {
                writeLock.unlock();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void returnObject(final K key, final V obj) {
            final WriteLock writeLock = readWriteLock.writeLock();
            writeLock.lock();
            try {
                keyedPool.returnObject(key, obj);
            } catch (final Exception e) {
                // swallowed
            } finally {
                writeLock.unlock();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void invalidateObject(final K key, final V obj) {
            final WriteLock writeLock = readWriteLock.writeLock();
            writeLock.lock();
            try {
                keyedPool.invalidateObject(key, obj);
            } catch (final Exception e) {
                // swallowed as of Pool 2
            } finally {
                writeLock.unlock();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addObject(final K key) throws Exception,
                IllegalStateException, UnsupportedOperationException {
            final WriteLock writeLock = readWriteLock.writeLock();
            writeLock.lock();
            try {
                keyedPool.addObject(key);
            } finally {
                writeLock.unlock();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getNumIdle(final K key) {
            final ReadLock readLock = readWriteLock.readLock();
            readLock.lock();
            try {
                return keyedPool.getNumIdle(key);
            } finally {
                readLock.unlock();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getNumActive(final K key) {
            final ReadLock readLock = readWriteLock.readLock();
            readLock.lock();
            try {
                return keyedPool.getNumActive(key);
            } finally {
                readLock.unlock();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getNumIdle() {
            final ReadLock readLock = readWriteLock.readLock();
            readLock.lock();
            try {
                return keyedPool.getNumIdle();
            } finally {
                readLock.unlock();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getNumActive() {
            final ReadLock readLock = readWriteLock.readLock();
            readLock.lock();
            try {
                return keyedPool.getNumActive();
            } finally {
                readLock.unlock();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void clear() throws Exception, UnsupportedOperationException {
            final WriteLock writeLock = readWriteLock.writeLock();
            writeLock.lock();
            try {
                keyedPool.clear();
            } finally {
                writeLock.unlock();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void clear(final K key) throws Exception,
                UnsupportedOperationException {
            final WriteLock writeLock = readWriteLock.writeLock();
            writeLock.lock();
            try {
                keyedPool.clear(key);
            } finally {
                writeLock.unlock();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void close() {
            final WriteLock writeLock = readWriteLock.writeLock();
            writeLock.lock();
            try {
                keyedPool.close();
            } catch (final Exception e) {
                // swallowed as of Pool 2
            } finally {
                writeLock.unlock();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("SynchronizedKeyedObjectPool");
            sb.append("{keyedPool=").append(keyedPool);
            sb.append('}');
            return sb.toString();
        }
    }

    private static final class SynchronizedKeyedPooledObjectFactory<K, V>
            implements KeyedPooledObjectFactory<K, V> {
        /**
         * Synchronization lock
         */
        private final WriteLock writeLock = new ReentrantReadWriteLock().writeLock();

        /**
         * Wrapped factory
         */
        private final KeyedPooledObjectFactory<K, V> keyedFactory;

        /**
         * Create a SynchronizedKeyedPoolableObjectFactory wrapping the given
         * factory.
         *
         * @param keyedFactory underlying factory to wrap
         * @throws IllegalArgumentException if the factory is null
         */
        SynchronizedKeyedPooledObjectFactory(
                final KeyedPooledObjectFactory<K, V> keyedFactory)
                throws IllegalArgumentException {
            if (keyedFactory == null) {
                throw new IllegalArgumentException(
                        "keyedFactory must not be null.");
            }
            this.keyedFactory = keyedFactory;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PooledObject<V> makeObject(final K key) throws Exception {
            writeLock.lock();
            try {
                return keyedFactory.makeObject(key);
            } finally {
                writeLock.unlock();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void destroyObject(final K key, final PooledObject<V> p) throws Exception {
            writeLock.lock();
            try {
                keyedFactory.destroyObject(key, p);
            } finally {
                writeLock.unlock();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean validateObject(final K key, final PooledObject<V> p) {
            writeLock.lock();
            try {
                return keyedFactory.validateObject(key, p);
            } finally {
                writeLock.unlock();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void activateObject(final K key, final PooledObject<V> p) throws Exception {
            writeLock.lock();
            try {
                keyedFactory.activateObject(key, p);
            } finally {
                writeLock.unlock();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void passivateObject(final K key, final PooledObject<V> p) throws Exception {
            writeLock.lock();
            try {
                keyedFactory.passivateObject(key, p);
            } finally {
                writeLock.unlock();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("SynchronizedKeyedPoolableObjectFactory");
            sb.append("{keyedFactory=").append(keyedFactory);
            sb.append('}');
            return sb.toString();
        }
    }

    private static final class ErodingFactor {
        /**
         * Determines frequency of "erosion" events
         */
        private final float factor;

        /**
         * Time of next shrink event
         */
        private transient volatile long nextShrink;

        /**
         * High water mark - largest numIdle encountered
         */
        private transient volatile int idleHighWaterMark;

        /**
         * Create a new ErodingFactor with the given erosion factor.
         *
         * @param factor erosion factor
         */
        public ErodingFactor(final float factor) {
            this.factor = factor;
            nextShrink = System.currentTimeMillis() + (long) (900000 * factor); // now
            // +
            // 15
            // min
            // *
            // factor
            idleHighWaterMark = 1;
        }

        /**
         * Updates internal state using the supplied time and numIdle.
         *
         * @param now     current time
         * @param numIdle number of idle elements in the pool
         */
        public void update(final long now, final int numIdle) {
            final int idle = Math.max(0, numIdle);
            idleHighWaterMark = Math.max(idle, idleHighWaterMark);
            final float maxInterval = 15f;
            final float minutes = maxInterval +
                    ((1f - maxInterval) / idleHighWaterMark) * idle;
            nextShrink = now + (long) (minutes * 60000f * factor);
        }

        /**
         * Returns the time of the next erosion event.
         *
         * @return next shrink time
         */
        public long getNextShrink() {
            return nextShrink;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "ErodingFactor{" + "factor=" + factor +
                    ", idleHighWaterMark=" + idleHighWaterMark + '}';
        }
    }

    private static class ErodingKeyedObjectPool<K, V> implements
            KeyedObjectPool<K, V> {
        /**
         * Underlying pool
         */
        private final KeyedObjectPool<K, V> keyedPool;

        /**
         * Erosion factor
         */
        private final ErodingFactor erodingFactor;

        public ErodingKeyedObjectPool(final KeyedObjectPool<K, V> keyedPool,
                                      final float factor) {
            this(keyedPool, new ErodingFactor(factor));
        }

        protected ErodingKeyedObjectPool(final KeyedObjectPool<K, V> keyedPool,
                                         final ErodingFactor erodingFactor) {
            if (keyedPool == null) {
                throw new IllegalArgumentException(
                        "keyedPool must not be null.");
            }
            this.keyedPool = keyedPool;
            this.erodingFactor = erodingFactor;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V borrowObject(final K key) throws Exception,
                NoSuchElementException, IllegalStateException {
            return keyedPool.borrowObject(key);
        }

        @Override
        public void returnObject(final K key, final V obj) throws Exception {
            boolean discard = false;
            final long now = System.currentTimeMillis();
            final ErodingFactor factor = getErodingFactor(key);
            synchronized (keyedPool) {
                if (factor.getNextShrink() < now) {
                    final int numIdle = getNumIdle(key);
                    if (numIdle > 0) {
                        discard = true;
                    }

                    factor.update(now, numIdle);
                }
            }
            try {
                if (discard) {
                    keyedPool.invalidateObject(key, obj);
                } else {
                    keyedPool.returnObject(key, obj);
                }
            } catch (final Exception e) {
                // swallowed
            }
        }

        protected ErodingFactor getErodingFactor(final K key) {
            return erodingFactor;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void invalidateObject(final K key, final V obj) {
            try {
                keyedPool.invalidateObject(key, obj);
            } catch (final Exception e) {
                // swallowed
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addObject(final K key) throws Exception,
                IllegalStateException, UnsupportedOperationException {
            keyedPool.addObject(key);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getNumIdle() {
            return keyedPool.getNumIdle();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getNumIdle(final K key) {
            return keyedPool.getNumIdle(key);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getNumActive() {
            return keyedPool.getNumActive();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getNumActive(final K key) {
            return keyedPool.getNumActive(key);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void clear() throws Exception, UnsupportedOperationException {
            keyedPool.clear();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void clear(final K key) throws Exception,
                UnsupportedOperationException {
            keyedPool.clear(key);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void close() {
            try {
                keyedPool.close();
            } catch (final Exception e) {
                // swallowed
            }
        }

        /**
         * Returns the underlying pool
         *
         * @return the keyed pool that this ErodingKeyedObjectPool wraps
         */
        protected KeyedObjectPool<K, V> getKeyedPool() {
            return keyedPool;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "ErodingKeyedObjectPool{" + "factor=" +
                    erodingFactor + ", keyedPool=" + keyedPool + '}';
        }
    }

    private static final class ErodingPerKeyKeyedObjectPool<K, V> extends
            ErodingKeyedObjectPool<K, V> {
        /**
         * Erosion factor - same for all pools
         */
        private final float factor;

        /**
         * Map of ErodingFactor instances keyed on pool keys
         */
        private final Map<K, ErodingFactor> factors = Collections.synchronizedMap(new HashMap<K, ErodingFactor>());

        /**
         * Create a new ErordingPerKeyKeyedObjectPool decorating the given keyed
         * pool with the specified erosion factor.
         *
         * @param keyedPool underlying keyed pool
         * @param factor    erosion factor
         */
        public ErodingPerKeyKeyedObjectPool(
                final KeyedObjectPool<K, V> keyedPool, final float factor) {
            super(keyedPool, null);
            this.factor = factor;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ErodingFactor getErodingFactor(final K key) {
            ErodingFactor eFactor = factors.get(key);
            // this may result in two ErodingFactors being created for a key
            // since they are small and cheap this is okay.
            if (eFactor == null) {
                eFactor = new ErodingFactor(this.factor);
                factors.put(key, eFactor);
            }
            return eFactor;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "ErodingPerKeyKeyedObjectPool{" + "factor=" + factor +
                    ", keyedPool=" + getKeyedPool() + '}';
        }
    }
}