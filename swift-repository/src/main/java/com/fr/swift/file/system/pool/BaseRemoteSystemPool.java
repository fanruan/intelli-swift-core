package com.fr.swift.file.system.pool;

import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.third.org.apache.commons.pool2.impl.GenericKeyedObjectPool;

import java.net.URI;

/**
 * @author yee
 * @date 2018/7/5
 */
public class BaseRemoteSystemPool<T extends SwiftFileSystem> extends GenericKeyedObjectPool<URI, T> {
    public BaseRemoteSystemPool(BaseRemoteSystemPoolFactory factory) {
        super(factory);
    }

    @Override
    public T borrowObject(URI key) {
        try {
            return super.borrowObject(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void returnObject(URI key, T obj) {
        try {
            super.returnObject(key, obj);
        } catch (IllegalStateException ignore) {
//            SwiftLoggers.getLogger().warn(e.getMessage());
        }
    }
}
