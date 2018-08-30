package com.fr.swift.jdbc.rpc.invoke;

import com.fr.swift.jdbc.rpc.nio.RpcConnector;
import com.fr.swift.jdbc.rpc.pool.BaseKeyedPooledObjectFactory;
import com.fr.swift.jdbc.rpc.pool.PooledObject;
import com.fr.swift.jdbc.rpc.pool.impl.DefaultPooledObject;

/**
 * @author yee
 * @date 2018/8/30
 */
public class ClientProxyPooledFactory extends BaseKeyedPooledObjectFactory<String, ClientProxy> {
    @Override
    public ClientProxy create(String key) throws Exception {
        ClientProxy proxy = new ClientProxy(new RpcConnector(key));
        proxy.start();
        return proxy;
    }

    @Override
    public PooledObject<ClientProxy> wrap(ClientProxy value) {
        return new DefaultPooledObject<ClientProxy>(value);
    }

    @Override
    public void destroyObject(String key, PooledObject<ClientProxy> p) throws Exception {
        p.invalidate();
        p.getObject().stop();
    }
}
