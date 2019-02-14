package com.fr.swift.file.system.pool;

import com.fr.swift.file.system.SwiftFileSystem;

/**
 * @author yee
 * @date 2018-12-03
 */
public interface RemoteFileSystemPool<S extends SwiftFileSystem> {
    S borrowObject(String path);

    void returnObject(String resourceURI, S fileSystem);
}
