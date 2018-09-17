package com.fr.swift.file.system.factory;

import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.repository.SwiftFileSystemConfig;

/**
 * @author yee
 * @date 2018/8/21
 */
public interface SwiftFileSystemFactory<System extends SwiftFileSystem, Config extends SwiftFileSystemConfig> {
    System createFileSystem(Config config, String path);

    void closeFileSystem(System fileSystem);
}
