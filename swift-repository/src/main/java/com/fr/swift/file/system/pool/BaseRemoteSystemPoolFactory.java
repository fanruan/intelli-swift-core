package com.fr.swift.file.system.pool;

import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.third.org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import com.fr.third.org.apache.commons.pool2.PooledObject;
import com.fr.third.org.apache.commons.pool2.impl.DefaultPooledObject;

import java.net.URI;

/**
 * @author yee
 * @date 2018/7/5
 */
public abstract class BaseRemoteSystemPoolFactory<T extends SwiftFileSystem> extends BaseKeyedPooledObjectFactory<URI, T> {
    @Override
    public PooledObject<T> wrap(T t) {
        return new DefaultPooledObject<T>(t);
    }

    @Override
    public void destroyObject(URI key, PooledObject<T> p) throws Exception {
        p.getObject().close();
    }
}
