package com.fr.swift.file.system.factory;

import com.fr.swift.config.bean.SwiftFileSystemConfig;
import com.fr.swift.file.system.SwiftFileSystem;

/**
 * @author yee
 * @date 2018/8/21
 */
public interface SwiftFileSystemFactory<System extends SwiftFileSystem, Config extends SwiftFileSystemConfig> {
    System createFileSystem(Config config, String path);

    void closeFileSystem(System fileSystem);
}
