package com.fr.swift.config;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.CommonConnectorConfig;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.config.service.SwiftFineIOConnectorService;
import com.fr.swift.cube.io.impl.fineio.connector.CommonConnectorType;
import com.fr.swift.cube.io.impl.fineio.connector.annotation.ConnectorBuilder;
import com.fr.swift.cube.io.impl.fineio.connector.annotation.PackConnectorBuilder;
import com.fr.swift.cube.io.impl.fineio.connector.builder.FineIOConnectorBuilder;
import com.fr.swift.cube.io.impl.fineio.connector.builder.PackageConnectorBuilder;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.util.Strings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * @author yee
 * @date 2019-01-02
 */
public class PublicConfig {
    /**
     * initMethod 好像没调用
     */
    public static void load() {
        SwiftFineIOConnectorService fineIoService = SwiftContext.get().getBean(SwiftFineIOConnectorService.class);
        // 优先读取jar外面的 即当前目录下的文件
        File configFile = new File("public.conf");
        InputStream is = null;
        try {
            is = new FileInputStream(configFile);
        } catch (FileNotFoundException e) {
            is = PublicConfig.class.getClassLoader().getResourceAsStream("public.conf");
        }
        try (InputStream publicIs = is) {
            if (null != publicIs) {
                Properties properties = new Properties();
                properties.load(publicIs);
                initPackageConnector(fineIoService, properties);
                initFineIOConnector(fineIoService, properties);
            }
        } catch (IOException e) {
            SwiftLoggers.getLogger().warn(e);
        }
    }

    private static void initFineIOConnector(SwiftFineIOConnectorService fineIoService, Properties properties) {
        String fineIoType = properties.getProperty("fineio.type", "LZ4");
        FineIOConnectorConfig config = null;
        try {
            CommonConnectorType type = CommonConnectorType.valueOf(fineIoType.toUpperCase());
            config = new CommonConnectorConfig(type);
        } catch (Exception ignore) {
            FineIOConnectorBuilder loader = getConnectorBuilder(fineIoType);
            if (null != loader) {
                config = loader.loadFromProperties(properties);
            }
        }
        if (null != config) {
            fineIoService.setCurrentConfig(config, SwiftFineIOConnectorService.Type.CONNECTOR);
        }
    }

    private static void initPackageConnector(SwiftFineIOConnectorService fineIoService, Properties properties) {
        if (SwiftProperty.getProperty().isCluster()) {
            String repoType = properties.getProperty("package.type");
            if (Strings.isNotEmpty(repoType)) {
                PackageConnectorBuilder factory = getPackConnectorBuilder(repoType);
                if (null != factory) {
                    FineIOConnectorConfig config = (FineIOConnectorConfig) factory.loadFromProperties(properties);
                    fineIoService.setCurrentConfig(config, SwiftFineIOConnectorService.Type.PACKAGE);
                }
            }
        }
    }

    private static PackageConnectorBuilder getPackConnectorBuilder(String packageType) {
        final Map<String, Object> beansByAnnotations = SwiftContext.get().getBeansByAnnotations(PackConnectorBuilder.class);
        for (Map.Entry<String, Object> entry : beansByAnnotations.entrySet()) {
            final Object value = entry.getValue();
            final PackConnectorBuilder annotation = value.getClass().getAnnotation(PackConnectorBuilder.class);
            if (annotation.value().equals(packageType)) {
                return (PackageConnectorBuilder) value;
            }
        }
        return null;

    }

    private static FineIOConnectorBuilder getConnectorBuilder(String packageType) {
        final Map<String, Object> beansByAnnotations = SwiftContext.get().getBeansByAnnotations(ConnectorBuilder.class);
        for (Map.Entry<String, Object> entry : beansByAnnotations.entrySet()) {
            final Object value = entry.getValue();
            final ConnectorBuilder annotation = value.getClass().getAnnotation(ConnectorBuilder.class);
            if (annotation.value().equals(packageType)) {
                return (FineIOConnectorBuilder) value;
            }
        }
        return null;
    }
}
