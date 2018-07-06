package com.fr.swift.config.bean;

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
