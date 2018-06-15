package com.fr.swift.file.conf.impl;

import com.fr.swift.file.conf.AbstractSwiftFileSystemConfig;
import com.fr.swift.file.system.SwiftFileSystemType;

/**
 * @author yee
 * @date 2018/6/15
 */
public class HdfsRepositoryConfigImpl extends AbstractSwiftFileSystemConfig {
    @Override
    public SwiftFileSystemType getType() {
        return SwiftFileSystemType.HDFS;
    }
}
