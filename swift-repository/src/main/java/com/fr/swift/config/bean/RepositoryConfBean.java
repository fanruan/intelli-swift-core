package com.fr.swift.config.bean;


import com.fr.swift.file.conf.SwiftFileSystemConfig;

/**
 * @author yee
 * @date 2018/6/15
 */
public interface RepositoryConfBean<T extends SwiftFileSystemConfig> extends Convert<T> {
    String getNameSpace();
}
