package com.fr.swift.file;

import com.aliyun.oss.OSS;
import com.fr.swift.repository.config.OssRepositoryConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * @author yee
 * @date 2018-12-20
 */
public class OssClientPool extends GenericObjectPool<OSS> {
    private OssRepositoryConfig config;

    public OssClientPool(OssRepositoryConfig config) {
        super(new OssClientPooledFactory(config));
        this.config = config;
    }

    public OssRepositoryConfig getConfig() {
        return config;
    }
}
