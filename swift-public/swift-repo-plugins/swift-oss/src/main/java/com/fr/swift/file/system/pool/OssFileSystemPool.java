package com.fr.swift.file.system.pool;

import com.fr.swift.file.OssClientPool;
import com.fr.swift.file.system.impl.OssFileSystemImpl;
import com.fr.swift.repository.config.OssRepositoryConfig;

/**
 * @author yee
 * @date 2019-01-21
 */
public class OssFileSystemPool extends BaseRemoteFileSystemPool<OssFileSystemImpl> {
    public OssFileSystemPool(OssRepositoryConfig config) {
        super(new OssFileSystemPoolFactory(config));
    }

    public static class OssFileSystemPoolFactory extends BaseRemoteSystemPoolFactory<OssFileSystemImpl> {
        private OssRepositoryConfig config;
        private OssClientPool clientPool;

        public OssFileSystemPoolFactory(OssRepositoryConfig config) {
            this.config = config;
            this.clientPool = new OssClientPool(config);
        }

        @Override
        public OssFileSystemImpl create(String s) throws Exception {
            return new OssFileSystemImpl(config, s, clientPool, RemoteFileSystemFactoryCreator.creator().getFactory(config.getType()));
        }
    }
}
