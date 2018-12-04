package com.fr.swift.file.system.pool;

import com.fr.swift.file.system.SwiftFileSystem;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

/**
 * @author yee
 * @date 2018/7/5
 */
public class BaseRemoteSystemPool<T extends SwiftFileSystem> extends GenericKeyedObjectPool<String, T> implements RemoteSystemPool<T> {
    public BaseRemoteSystemPool(BaseRemoteSystemPoolFactory factory) {
        super(factory);
    }

    @Override
    public T borrowObject(String key) {
        try {
            return super.borrowObject(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void returnObject(String key, T obj) {
        try {
            super.returnObject(key, obj);
        } catch (IllegalStateException ignore) {
//            SwiftLoggers.getLogger().warn(e.getMessage());
        }
    }
}
