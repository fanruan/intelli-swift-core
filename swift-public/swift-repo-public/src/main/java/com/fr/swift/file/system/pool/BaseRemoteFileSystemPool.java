package com.fr.swift.file.system.pool;

import com.fr.swift.file.system.SwiftFileSystem;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * @author yee
 * @date 2018/7/5
 */
public class BaseRemoteFileSystemPool<T extends SwiftFileSystem> extends GenericKeyedObjectPool<String, T> implements RemoteFileSystemPool<T> {
    public BaseRemoteFileSystemPool(BaseRemoteSystemPoolFactory factory) {
        super(factory);
    }

    public BaseRemoteFileSystemPool(KeyedPooledObjectFactory<String, T> factory, GenericKeyedObjectPoolConfig config) {
        super(factory, config);
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
        } catch (Exception ignore) {
//            SwiftLoggers.getLogger().warn(e.getMessage());
        }
    }
}
