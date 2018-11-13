package com.fr.swift.api.rpc.pool;

import com.fr.swift.api.rpc.invoke.CallClient;
import com.fr.third.org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import com.fr.third.org.apache.commons.pool2.PooledObject;
import com.fr.third.org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author yee
 * @date 2018/8/29
 */
public class CallClientPooledFactory extends BaseKeyedPooledObjectFactory<String, CallClient> {

    private int maxFrameSize;

    public CallClientPooledFactory(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }

    @Override
    public CallClient create(String s) throws Exception {
        CallClient client = new CallClient(s, maxFrameSize);
        client.bind();
        return client;
    }

    @Override
    public PooledObject<CallClient> wrap(CallClient callClient) {
        return new DefaultPooledObject<CallClient>(callClient);
    }

    @Override
    public void destroyObject(String key, PooledObject<CallClient> p) {
        p.getObject().close();
    }
}
