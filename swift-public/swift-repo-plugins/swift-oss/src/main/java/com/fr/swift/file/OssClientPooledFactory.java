package com.fr.swift.file;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.fr.swift.repository.config.OssRepositoryConfig;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author yee
 * @date 2018-12-20
 */
class OssClientPooledFactory extends BasePooledObjectFactory<OSS> {
    private OssRepositoryConfig config;

    public OssClientPooledFactory(OssRepositoryConfig config) {
        this.config = config;
    }

    @Override
    public OSS create() throws Exception {
        return new OSSClientBuilder().build(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret());
    }

    @Override
    public PooledObject<OSS> wrap(OSS ossClient) {
        return new DefaultPooledObject<OSS>(ossClient);
    }

    @Override
    public void destroyObject(PooledObject<OSS> p) throws Exception {
        p.getObject().shutdown();
    }
}
