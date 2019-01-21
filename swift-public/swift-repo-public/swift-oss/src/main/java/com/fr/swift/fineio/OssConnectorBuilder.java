package com.fr.swift.fineio;

import com.fineio.storage.Connector;
import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.cube.io.impl.fineio.connector.annotation.ConnectorBuilder;
import com.fr.swift.cube.io.impl.fineio.connector.builder.BaseConnectorBuilder;
import com.fr.swift.file.OssClientPool;
import com.fr.swift.repository.SwiftFileSystemConfig;
import com.fr.swift.repository.config.OssRepositoryConfig;
import com.fr.swift.repository.config.OssType;
import com.fr.swift.service.SwiftRepositoryConfService;
import com.fr.swift.util.Util;

import java.util.Properties;

/**
 * @author yee
 * @date 2018-12-20
 */
@ConnectorBuilder("OSS")
public class OssConnectorBuilder extends BaseConnectorBuilder {
    private OssRepositoryConfig config;

    @Override
    public Connector build() {
        Util.requireNonNull(config);
        return new OssConnector(new OssClientPool(config));
    }

    @Override
    public FineIOConnectorConfig loadFromProperties(Properties properties) {
        SwiftFileSystemConfig config = SwiftContext.get().getBean(SwiftRepositoryConfService.class).getCurrentRepository();
        if (null != config && config.getType().equals(OssType.OSS)) {
            OssRepositoryConfig ossConfig = (OssRepositoryConfig) config;
            ossConfig.setBucketName(properties.getProperty("fineio.bucketName", ossConfig.getBucketName()));
            ossConfig.setAccessKeyId(properties.getProperty("fineio.accessKeyId", ossConfig.getAccessKeyId()));
            ossConfig.setAccessKeySecret(properties.getProperty("fineio.accessKeySecret", ossConfig.getAccessKeySecret()));
            ossConfig.setEndpoint(properties.getProperty("fineio.endpoint", ossConfig.getEndpoint()));
            this.config = ossConfig;
            return ossConfig;
        } else {
            String bucketName = properties.getProperty("fineio.bucketName");
            String accessKeyId = properties.getProperty("fineio.accessKeyId");
            String accessKeySecret = properties.getProperty("fineio.accessKeySecret");
            String endpoint = properties.getProperty("fineio.endpoint");
            Util.requireNonNull(bucketName, accessKeyId, accessKeySecret, endpoint);
            OssRepositoryConfig ossConfig = new OssRepositoryConfig();
            ossConfig.setEndpoint(endpoint);
            ossConfig.setAccessKeyId(accessKeyId);
            ossConfig.setAccessKeySecret(accessKeySecret);
            ossConfig.setBucketName(bucketName);
            this.config = ossConfig;
            return ossConfig;
        }
    }
}
