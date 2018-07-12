package com.fr.swift.file.system.pool;

import com.fr.swift.config.bean.HdfsRepositoryConfigBean;
import com.fr.swift.file.system.impl.HdfsFileSystemImpl;
import com.fr.third.org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import com.fr.third.org.apache.commons.pool2.KeyedObjectPool;
import com.fr.third.org.apache.commons.pool2.PooledObject;
import com.fr.third.org.apache.commons.pool2.impl.DefaultPooledObject;
import com.fr.third.org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import java.net.URI;

/**
 * @author yee
 * @date 2018/7/5
 */
class HdfsFileSystemPool extends BaseRemoteSystemPool {

    HdfsFileSystemPool(HdfsRepositoryConfigBean config) {
        super(new HdfsFileSystemPoolFactory(config));
    }

    private static class HdfsFileSystemPoolFactory extends BaseRemoteSystemPoolFactory<HdfsFileSystemImpl> {
        private HdfsRepositoryConfigBean config;
        private KeyedObjectPool<URI, FileSystem> clientPool;

        public HdfsFileSystemPoolFactory(final HdfsRepositoryConfigBean config) {
            final Configuration conf = new Configuration();
            conf.set(config.getFsName(), config.getFullAddress());
            clientPool = new GenericKeyedObjectPool<URI, FileSystem>(new BaseKeyedPooledObjectFactory<URI, FileSystem>() {
                @Override
                public FileSystem create(URI uri) throws Exception {
                    return FileSystem.get(URI.create(config.getFullAddress() + uri.getPath()), conf);
                }

                @Override
                public PooledObject<FileSystem> wrap(FileSystem fileSystem) {
                    return new DefaultPooledObject<FileSystem>(fileSystem);
                }

                @Override
                public void destroyObject(URI key, PooledObject<FileSystem> p) throws Exception {
                    p.getObject().close();
                }
            });
            this.config = config;
        }

        @Override
        public HdfsFileSystemImpl create(URI uri) {
            return new HdfsFileSystemImpl(config, uri, clientPool);
//            return null;
        }
    }
}
