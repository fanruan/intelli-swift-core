package com.fr.swift.netty.rpc.pool;

import com.fr.swift.netty.rpc.client.AbstractRpcClientHandler;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * This class created on 2018/8/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class AbstractRpcPool implements KeyedObjectPool<String, AbstractRpcClientHandler> {

    private static final long IDLE_OBJ_EXPIRE_TIME = 20000L;
    protected GenericKeyedObjectPool keyedObjectPool;

    public AbstractRpcPool(AbstractRpcKeyPoolFactory rpcKeyPoolFactory) {
        GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
        config.setTimeBetweenEvictionRunsMillis(IDLE_OBJ_EXPIRE_TIME);
        config.setMinEvictableIdleTimeMillis(IDLE_OBJ_EXPIRE_TIME);
        keyedObjectPool = new GenericKeyedObjectPool(rpcKeyPoolFactory, config);
    }

    @Override
    public AbstractRpcClientHandler borrowObject(String key) throws Exception {
        AbstractRpcClientHandler handler = (AbstractRpcClientHandler) keyedObjectPool.borrowObject(key);
        return handler;
    }

    @Override
    public void returnObject(String key, AbstractRpcClientHandler handler) {
        keyedObjectPool.returnObject(key, handler);
    }

    @Override
    public void invalidateObject(String key, AbstractRpcClientHandler handler) throws Exception {
        keyedObjectPool.invalidateObject(key, handler);
    }

    @Override
    public void addObject(String key) throws Exception {
        keyedObjectPool.addObject(key);
    }

    @Override
    public int getNumIdle(String key) {
        return keyedObjectPool.getNumIdle(key);
    }

    @Override
    public int getNumActive(String key) {
        return keyedObjectPool.getNumActive(key);
    }

    @Override
    public int getNumIdle() {
        return keyedObjectPool.getNumIdle();
    }

    @Override
    public int getNumActive() {
        return keyedObjectPool.getNumActive();
    }

    @Override
    public void clear() {
        keyedObjectPool.clear();
    }

    @Override
    public void clear(String key) {
        keyedObjectPool.clear(key);
    }

    @Override
    public void close() {
        keyedObjectPool.close();
    }
}
