package com.fr.swift.file.system.factory;

import com.fr.swift.file.system.annotation.FileSystemFactory;
import com.fr.swift.file.system.impl.HdfsFileSystemImpl;
import com.fr.swift.file.system.pool.BaseRemoteSystemPool;
import com.fr.swift.file.system.pool.HdfsFileSystemPool;
import com.fr.swift.repository.config.HdfsRepositoryConfig;

/**
 * @author yee
 * @date 2018/8/21
 */
@FileSystemFactory(name = "HDFS")
public class HdfsFileSystemFactory extends BasePooledFileSystemFactory<HdfsFileSystemImpl, HdfsRepositoryConfig> {
    @Override
    protected BaseRemoteSystemPool<HdfsFileSystemImpl> createPool(HdfsRepositoryConfig config) {
        return new HdfsFileSystemPool(config);
    }
}
