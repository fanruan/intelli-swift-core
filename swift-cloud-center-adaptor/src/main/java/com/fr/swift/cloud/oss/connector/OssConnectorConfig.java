package com.fr.swift.cloud.oss.connector;

import com.fr.stable.StringUtils;
import com.fr.swift.config.bean.FineIOConnectorConfig;

/**
 * @author yee
 * @date 2018-12-20
 */
public class OssConnectorConfig implements FineIOConnectorConfig {
    public static final String OSS = "OSS";
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    @Override
    public String type() {
        return OSS;
    }

    @Override
    public String basePath() {
        return StringUtils.EMPTY;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
