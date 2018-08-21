package com.fr.swift.file.system.pool;

import com.fr.ftp.client.FineFTP;
import com.fr.ftp.config.FTPConfig;
import com.fr.ftp.pool.FineFTPClientFactory;
import com.fr.ftp.pool.FineFTPPoolConfig;
import com.fr.ftp.pool.GenericFineFTPPool;
import com.fr.swift.file.system.impl.FtpFileSystemImpl;
import com.fr.swift.repository.config.FtpRepositoryConfig;
import com.fr.third.org.apache.commons.pool2.ObjectPool;
import com.fr.third.org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @author yee
 * @date 2018/7/5
 */
public class FtpFileSystemPool extends BaseRemoteSystemPool<FtpFileSystemImpl> {

    public FtpFileSystemPool(FtpRepositoryConfig config) {
        super(new FtpFileSystemPoolFactory(config));
    }

    private static class FtpFileSystemPoolFactory extends BaseRemoteSystemPoolFactory<FtpFileSystemImpl> {
        private FtpRepositoryConfig config;
        private ObjectPool<FineFTP> clientPool;

        public FtpFileSystemPoolFactory(FtpRepositoryConfig config) {
            FTPConfig ftpConfig = config.toFtpConfig();
            GenericObjectPoolConfig poolConfig = FineFTPPoolConfig.getPoolConfig();
            poolConfig.setTestOnBorrow(true);
            FineFTPClientFactory factory = new FineFTPClientFactory(ftpConfig);
            clientPool = new GenericFineFTPPool(factory, poolConfig);
            this.config = config;
        }

        @Override
        public FtpFileSystemImpl create(String uri) {
            return new FtpFileSystemImpl(config, uri, clientPool);
        }
    }
}
