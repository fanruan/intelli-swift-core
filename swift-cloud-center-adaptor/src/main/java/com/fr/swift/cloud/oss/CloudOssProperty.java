package com.fr.swift.cloud.oss;

import com.fr.swift.cloud.oss.connector.OssConnectorConfig;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.config.service.SwiftFineIOConnectorService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.IoUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author yee
 * @date 2018-12-20
 */
public class CloudOssProperty {
    private static final CloudOssProperty PROPERTY;

    static {
        PROPERTY = load();
    }

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    private CloudOssProperty() {
    }

    private static CloudOssProperty load() {
        SwiftFineIOConnectorService service = SwiftContext.get().getBean(SwiftFineIOConnectorService.class);
        FineIOConnectorConfig config = service.getCurrentConfig();
        boolean isOss = OssConnectorConfig.OSS.equals(config.type());
        InputStream is = CloudOssProperty.class.getClassLoader().getResourceAsStream("cloud-oss-config.properties");
        Properties properties = new Properties();
        try {
            if (null == is && isOss) {
                CloudOssProperty property = new CloudOssProperty();
                OssConnectorConfig ossConfig = (OssConnectorConfig) config;
                property.endpoint = ossConfig.getEndpoint();
                property.accessKeyId = ossConfig.getAccessKeyId();
                property.accessKeySecret = ossConfig.getAccessKeySecret();
                property.bucketName = ossConfig.getBucketName();
                return property;
            }
            properties.load(is);
            CloudOssProperty property = new CloudOssProperty();
            if (isOss) {
                property.loadFromProperties(properties, (OssConnectorConfig) config);
            } else {
                property.loadFromProperties(properties, null);
            }
            updateConfig(service, property);
            return property;
        } catch (IOException e) {
            return Crasher.crash(e);
        } finally {
            IoUtil.close(is);
        }
    }

    private static void updateConfig(SwiftFineIOConnectorService service, CloudOssProperty property) {
        OssConnectorConfig config = new OssConnectorConfig();
        config.setEndpoint(property.getEndpoint());
        config.setAccessKeyId(property.getAccessKeyId());
        config.setAccessKeySecret(property.getAccessKeySecret());
        config.setBucketName(property.getBucketName());
        service.setCurrentConfig(config);
    }

    public static CloudOssProperty getInstance() {
        return PROPERTY;
    }

    private void loadFromProperties(Properties properties, OssConnectorConfig connectorConfig) {
        if (null != connectorConfig) {
            endpoint = properties.getProperty("endpoint", connectorConfig.getEndpoint());
            accessKeyId = properties.getProperty("accessKeyId", connectorConfig.getAccessKeyId());
            accessKeySecret = properties.getProperty("accessKeySecret", connectorConfig.getAccessKeySecret());
            bucketName = properties.getProperty("bucketName", connectorConfig.getBucketName());
        } else {
            endpoint = properties.getProperty("endpoint");
            accessKeyId = properties.getProperty("accessKeyId");
            accessKeySecret = properties.getProperty("accessKeySecret");
            bucketName = properties.getProperty("bucketName");
        }
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }
}
