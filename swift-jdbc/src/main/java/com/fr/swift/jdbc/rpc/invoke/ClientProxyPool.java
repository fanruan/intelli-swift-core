package com.fr.swift.jdbc.rpc.invoke;


import com.fr.third.org.apache.commons.pool2.KeyedPooledObjectFactory;
import com.fr.third.org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import com.fr.third.org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * @author yee
 * @date 2018/8/30
 */
public class ClientProxyPool extends GenericKeyedObjectPool<String, ClientProxy> {

    private static final long IDLE_OBJ_EXPIRE_TIME = 20000L;
    private static ClientProxyPool instance;

    private ClientProxyPool(KeyedPooledObjectFactory<String, ClientProxy> factory, GenericKeyedObjectPoolConfig config) {
        super(factory, config);
    }

    public static ClientProxyPool newInstance(GenericKeyedObjectPoolConfig config) {
        return new ClientProxyPool(new ClientProxyPooledFactory(), config);
    }

    public static ClientProxyPool getInstance() {
        if (null == instance) {
            synchronized (ClientProxyPool.class) {
                if (null == instance) {
                    GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
                    config.setTimeBetweenEvictionRunsMillis(IDLE_OBJ_EXPIRE_TIME);
                    config.setMinEvictableIdleTimeMillis(IDLE_OBJ_EXPIRE_TIME);
                    instance = newInstance(config);
                }
            }
        }
        return instance;
    }

    @Override
    public void close() {
        super.close();
        if (null != instance) {
            instance = null;
        }
    }
}
