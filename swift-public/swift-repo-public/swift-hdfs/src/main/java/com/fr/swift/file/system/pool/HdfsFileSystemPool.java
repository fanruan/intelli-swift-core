package com.fr.swift.file.system.pool;

import com.fr.swift.file.system.impl.HdfsFileSystemImpl;
import com.fr.swift.repository.config.HdfsRepositoryConfig;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import java.net.URI;

/**
 * @author yee
 * @date 2018/7/5
 */
public class HdfsFileSystemPool extends BaseRemoteFileSystemPool {

    public HdfsFileSystemPool(HdfsRepositoryConfig config) {
        super(new HdfsFileSystemPoolFactory(config));
    }

    private static class HdfsFileSystemPoolFactory extends BaseRemoteSystemPoolFactory<HdfsFileSystemImpl> {
        private HdfsRepositoryConfig config;
        private KeyedObjectPool<String, FileSystem> clientPool;

        public HdfsFileSystemPoolFactory(final HdfsRepositoryConfig config) {
            final Configuration conf = new Configuration();
            conf.set(config.getFsName(), config.getFullAddress());
            clientPool = new GenericKeyedObjectPool<String, FileSystem>(new BaseKeyedPooledObjectFactory<String, FileSystem>() {
                @Override
                public FileSystem create(String uri) throws Exception {
                    return FileSystem.get(URI.create(config.getFullAddress() + uri), conf);
                }

                @Override
                public PooledObject<FileSystem> wrap(FileSystem fileSystem) {
                    return new DefaultPooledObject<FileSystem>(fileSystem);
                }

                @Override
                public void destroyObject(String key, PooledObject<FileSystem> p) throws Exception {
                    p.getObject().close();
                }
            });
            this.config = config;
        }

        @Override
        public HdfsFileSystemImpl create(String uri) {
            return new HdfsFileSystemImpl(config, uri, clientPool);
//            return null;
        }
    }
}
