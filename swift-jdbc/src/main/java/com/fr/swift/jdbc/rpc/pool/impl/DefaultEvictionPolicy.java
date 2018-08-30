package com.fr.swift.jdbc.rpc.pool.impl;

import com.fr.swift.jdbc.rpc.pool.PooledObject;

public class DefaultEvictionPolicy<T> implements EvictionPolicy<T> {

    @Override
    public boolean evict(final EvictionConfig config, final PooledObject<T> underTest,
                         final int idleCount) {

        if ((config.getIdleSoftEvictTime() < underTest.getIdleTimeMillis() &&
                config.getMinIdle() < idleCount) ||
                config.getIdleEvictTime() < underTest.getIdleTimeMillis()) {
            return true;
        }
        return false;
    }
}
