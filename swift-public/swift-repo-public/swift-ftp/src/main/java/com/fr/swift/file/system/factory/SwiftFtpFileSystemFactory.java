package com.fr.swift.file.system.factory;

import com.fr.swift.file.system.annotation.FileSystemFactory;
import com.fr.swift.file.system.impl.FtpFileSystemImpl;
import com.fr.swift.file.system.pool.FtpFileSystemPool;
import com.fr.swift.file.system.pool.RemoteFileSystemPool;
import com.fr.swift.repository.config.FtpRepositoryConfig;


/**
 * @author yee
 * @date 2018/8/21
 */
@FileSystemFactory(name = "FTP")
public class SwiftFtpFileSystemFactory extends BasePooledFileSystemFactory<FtpFileSystemImpl, FtpRepositoryConfig> {

    @Override
    protected RemoteFileSystemPool<FtpFileSystemImpl> createPool(FtpRepositoryConfig config) {
        return new FtpFileSystemPool(config);
    }
}
