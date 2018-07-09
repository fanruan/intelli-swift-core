package com.fr.swift.file.system.pool;

import com.fr.ftp.client.FineFTP;
import com.fr.ftp.config.FTPConfig;
import com.fr.ftp.pool.FineFTPClientFactory;
import com.fr.ftp.pool.FineFTPPoolConfig;
import com.fr.ftp.pool.GenericFineFTPPool;
import com.fr.swift.config.bean.FtpRepositoryConfigBean;
import com.fr.swift.file.system.impl.FtpFileSystemImpl;
import com.fr.third.org.apache.commons.pool2.ObjectPool;
import com.fr.third.org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.net.URI;

/**
 * @author yee
 * @date 2018/7/5
 */
class FtpFileSystemPool extends BaseRemoteSystemPool {

    FtpFileSystemPool(FtpRepositoryConfigBean config) {
        super(new FtpFileSystemPoolFactory(config));
    }

    private static class FtpFileSystemPoolFactory extends BaseRemoteSystemPoolFactory<FtpFileSystemImpl> {
        private FtpRepositoryConfigBean config;
        private ObjectPool<FineFTP> clientPool;

        public FtpFileSystemPoolFactory(FtpRepositoryConfigBean config) {
            FTPConfig ftpConfig = config.toFtpConfig();
            GenericObjectPoolConfig poolConfig = FineFTPPoolConfig.getPoolConfig();
            poolConfig.setTestOnBorrow(true);
            FineFTPClientFactory factory = new FineFTPClientFactory(ftpConfig);
            clientPool = new GenericFineFTPPool(factory, poolConfig);
            this.config = config;
        }

        @Override
        public FtpFileSystemImpl create(URI uri) {
            return new FtpFileSystemImpl(config, uri, clientPool);
        }
    }
}
