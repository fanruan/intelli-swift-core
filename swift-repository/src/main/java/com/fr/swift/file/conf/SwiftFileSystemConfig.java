package com.fr.swift.file.conf;

import com.fr.swift.file.system.SwiftFileSystemType;

/**
 * @author yee
 * @date 2018/5/28
 */
public interface SwiftFileSystemConfig {
    SwiftFileSystemType getType();

    SwiftFileSystemConfig DEFAULT = new SwiftFileSystemConfig() {

        @Override
        public SwiftFileSystemType getType() {
            return SwiftFileSystemType.FR;
        }
    };
}
