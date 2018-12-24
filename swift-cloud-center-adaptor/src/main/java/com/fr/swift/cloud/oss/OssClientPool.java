package com.fr.swift.cloud.oss;

import com.aliyun.oss.OSS;
import com.fr.third.org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * @author yee
 * @date 2018-12-20
 */
public class OssClientPool extends GenericObjectPool<OSS> {
    private static OssClientPool INSTANCE;

    private OssClientPool() {
        super(new OssClientPooledFactory());
    }

    public static OssClientPool getInstance() {
        if (null == INSTANCE || INSTANCE.isClosed()) {
            synchronized (OssClientPool.class) {
                if (null == INSTANCE || INSTANCE.isClosed()) {
                    INSTANCE = new OssClientPool();
                }
            }
        }
        return INSTANCE;
    }
}
