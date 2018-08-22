package com.fr.swift.repository.config;

import com.fr.swift.config.bean.SwiftFileSystemConfig;
import com.fr.swift.file.SwiftFileSystemType;
import com.fr.swift.file.SwiftRemoteFileSystemType;

/**
 * @author yee
 * @date 2018/8/21
 */
public enum DefaultRepositoryConfig implements SwiftFileSystemConfig {
    INSTANCE;

    @Override
    public SwiftFileSystemType getType() {
        return SwiftRemoteFileSystemType.FR;
    }
}
