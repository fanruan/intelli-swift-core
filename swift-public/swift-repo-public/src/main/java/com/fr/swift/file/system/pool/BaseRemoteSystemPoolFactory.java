package com.fr.swift.file.system.pool;

import com.fr.swift.file.system.SwiftFileSystem;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author yee
 * @date 2018/7/5
 */
public abstract class BaseRemoteSystemPoolFactory<T extends SwiftFileSystem> extends BaseKeyedPooledObjectFactory<String, T> {
    @Override
    public PooledObject<T> wrap(T t) {
        return new DefaultPooledObject<T>(t);
    }
}
