package com.fr.swift.file.system.factory;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.file.system.annotation.FileSystemFactory;
import com.fr.swift.file.system.impl.OssFileSystemImpl;
import com.fr.swift.file.system.pool.OssFileSystemPool;
import com.fr.swift.file.system.pool.RemoteFileSystemPool;
import com.fr.swift.repository.config.OssRepositoryConfig;
import com.fr.swift.util.Util;

import java.util.Properties;


/**
 * @author yee
 * @date 2018/8/21
 */
@FileSystemFactory(name = "OSS")
@SwiftBean(name = "OSS")
public class SwiftOssFileSystemFactory extends BasePooledFileSystemFactory<OssFileSystemImpl, OssRepositoryConfig> {

    @Override
    protected RemoteFileSystemPool<OssFileSystemImpl> createPool(OssRepositoryConfig config) {
        return new OssFileSystemPool(config);
    }

    @Override
    public OssRepositoryConfig loadFromProperties(Properties properties) {
        String endpoint = properties.getProperty("repo.endpoint");
        String accessKeyId = properties.getProperty("repo.accessKeyId");
        String accessKeySecret = properties.getProperty("repo.accessKeySecret");
        String bucketName = properties.getProperty("repo.bucketName");
        Util.requireNonNull(endpoint, accessKeyId, accessKeySecret, bucketName);
        OssRepositoryConfig config = new OssRepositoryConfig();
        config.setEndpoint(endpoint);
        config.setAccessKeyId(accessKeyId);
        config.setAccessKeySecret(accessKeySecret);
        config.setBucketName(bucketName);
        return config;
    }
}
