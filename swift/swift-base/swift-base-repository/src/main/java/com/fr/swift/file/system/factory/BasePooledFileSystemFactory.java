package com.fr.swift.file.system.factory;

import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.file.system.pool.RemoteFileSystemPool;
import com.fr.swift.repository.SwiftFileSystemConfig;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/8/21
 */
public abstract class BasePooledFileSystemFactory<S extends SwiftFileSystem, C extends SwiftFileSystemConfig> implements SwiftFileSystemFactory<S, C> {
    private ConcurrentHashMap<SwiftFileSystemConfig, RemoteFileSystemPool<S>> poolContainer;

    public BasePooledFileSystemFactory() {
        poolContainer = new ConcurrentHashMap<SwiftFileSystemConfig, RemoteFileSystemPool<S>>();
    }

    protected abstract RemoteFileSystemPool<S> createPool(C config);

    @Override
    public S createFileSystem(C config, String path) {
        if (null == poolContainer.get(config)) {
            poolContainer.put(config, this.createPool(config));
        }
        return poolContainer.get(config).borrowObject(path);
    }

    @Override
    public void closeFileSystem(S fileSystem) {
        if (null != fileSystem) {
            poolContainer.get(fileSystem.getConfig()).returnObject(fileSystem.getResourceURI(), fileSystem);
        }
    }
}
