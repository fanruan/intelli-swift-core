package com.fr.swift.repository.config;

import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.file.SwiftFileSystemType;
import com.fr.swift.repository.SwiftFileSystemConfig;
import com.fr.swift.util.Strings;

/**
 * @author yee
 * @date 2019-01-21
 */
public class OssRepositoryConfig implements SwiftFileSystemConfig, FineIOConnectorConfig {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

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

    @Override
    public SwiftFileSystemType getType() {
        return OssType.OSS;
    }

    @Override
    public String type() {
        return getType().name();
    }

    @Override
    public String basePath() {
        return Strings.EMPTY;
    }
}
