package com.fr.swift.api.rpc.pool;

import com.fr.swift.api.rpc.invoke.CallClient;
import com.fr.third.org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import com.fr.third.org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * @author yee
 * @date 2018/8/29
 */
public class CallClientPool extends GenericKeyedObjectPool<String, CallClient> {

    private static final long IDLE_OBJ_EXPIRE_TIME = 20000L;
    private static CallClientPool instance;

    private CallClientPool(int maxFrameSize, GenericKeyedObjectPoolConfig config) {
        super(new CallClientPooledFactory(maxFrameSize), config);
    }

    public static CallClientPool getInstance(int maxFrameSize) {
        if (null == instance) {
            synchronized (CallClientPool.class) {
                if (null == instance) {
                    GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
                    config.setTimeBetweenEvictionRunsMillis(IDLE_OBJ_EXPIRE_TIME);
                    config.setMinEvictableIdleTimeMillis(IDLE_OBJ_EXPIRE_TIME);
                    instance = new CallClientPool(maxFrameSize, config);
                }
            }
        }
        return instance;
    }

    @Override
    public void close() {
        super.close();
        instance = null;
    }
}
