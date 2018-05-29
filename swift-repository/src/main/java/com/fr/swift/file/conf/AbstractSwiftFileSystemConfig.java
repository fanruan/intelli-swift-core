package com.fr.swift.file.conf;

/**
 * @author yee
 * @date 2018/5/28
 */
public abstract class AbstractSwiftFileSystemConfig implements SwiftFileSystemConfig {
    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();
}
