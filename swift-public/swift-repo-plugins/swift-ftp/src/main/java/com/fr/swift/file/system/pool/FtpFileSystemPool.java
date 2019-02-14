package com.fr.swift.file.system.pool;

import com.fr.swift.file.SwiftRemoteFileSystemType;
import com.fr.swift.file.client.SwiftFTPClient;
import com.fr.swift.file.system.impl.FtpFileSystemImpl;
import com.fr.swift.file.system.pool.config.FtpClientPoolConfig;
import com.fr.swift.repository.config.FtpRepositoryConfig;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @author yee
 * @date 2018/7/5
 */
public class FtpFileSystemPool extends BaseRemoteFileSystemPool<FtpFileSystemImpl> {

    public FtpFileSystemPool(FtpRepositoryConfig config) {
        super(new FtpFileSystemPoolFactory(config));
    }

    private static class FtpFileSystemPoolFactory extends BaseRemoteSystemPoolFactory<FtpFileSystemImpl> {
        private FtpRepositoryConfig config;
        private ObjectPool<SwiftFTPClient> clientPool;

        public FtpFileSystemPoolFactory(FtpRepositoryConfig config) {
            GenericObjectPoolConfig poolConfig = new FtpClientPoolConfig().getPoolConfig();
            poolConfig.setTestOnBorrow(true);
            FtpClientPoolFactory factory = new FtpClientPoolFactory(config);
            clientPool = new GenericObjectPool<SwiftFTPClient>(factory, poolConfig);
            this.config = config;
        }

        @Override
        public FtpFileSystemImpl create(String uri) {
            return new FtpFileSystemImpl(config, uri, clientPool, RemoteFileSystemFactoryCreator.creator().getFactory(SwiftRemoteFileSystemType.FTP));
        }
    }
}
