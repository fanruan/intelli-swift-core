package com.fr.swift.cloud.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.fr.third.org.apache.commons.pool2.BasePooledObjectFactory;
import com.fr.third.org.apache.commons.pool2.PooledObject;
import com.fr.third.org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author yee
 * @date 2018-12-20
 */
class OssClientPooledFactory extends BasePooledObjectFactory<OSS> {
    @Override
    public OSS create() throws Exception {
        CloudOssProperty property = CloudOssProperty.getInstance();
        return new OSSClientBuilder().build(property.getEndpoint(), property.getAccessKeyId(), property.getAccessKeySecret());
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
