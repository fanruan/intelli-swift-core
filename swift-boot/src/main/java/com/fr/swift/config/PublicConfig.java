package com.fr.swift.config;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.CommonConnectorConfig;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.config.service.SwiftFineIOConnectorService;
import com.fr.swift.cube.io.impl.fineio.connector.CommonConnectorType;
import com.fr.swift.cube.io.impl.fineio.connector.builder.FineIOConnectorBuilder;
import com.fr.swift.cube.io.impl.fineio.connector.builder.PackageConnectorBuilder;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.util.IoUtil;
import com.fr.swift.util.Strings;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
            if (configFile.exists()) {
                is = new FileInputStream(configFile);
            } else {
                // 如果文件不存在就读jar里的
                is = PublicConfig.class.getClassLoader().getResourceAsStream("public.conf");
            }
            if (null != is) {
                Properties properties = new Properties();
                properties.load(is);
                try {
                    if (SwiftProperty.getProperty().isCluster()) {
                        String repoType = properties.getProperty("package.type");
                        if (Strings.isNotEmpty(repoType)) {
                            PackageConnectorBuilder factory = SwiftContext.get().getBean(repoType, PackageConnectorBuilder.class);
                            if (null != factory) {
                                FineIOConnectorConfig config = (FineIOConnectorConfig) factory.loadFromProperties(properties);
                                fineIoService.setCurrentConfig(config, SwiftConfigConstants.Namespace.FINE_IO_PACKAGE);
                            }
                        }
                    }
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error("load repo config failed!", e);
                }
                try {
                    String fineIoType = properties.getProperty("fineio.type", "LZ4");
                    FineIOConnectorConfig config = null;
                    try {
                        CommonConnectorType type = CommonConnectorType.valueOf(fineIoType.toUpperCase());
                        config = new CommonConnectorConfig(type);
                    } catch (Exception ignore) {
                        FineIOConnectorBuilder loader = SwiftContext.get().getBean(fineIoType, FineIOConnectorBuilder.class);
                        if (null != loader) {
                            config = loader.loadFromProperties(properties);
                        }
                    }
                    if (null != config) {
                        fineIoService.setCurrentConfig(config, SwiftConfigConstants.Namespace.FINE_IO_CONNECTOR);
                    }
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error("load fineio config failed!", e);
                }
            }
        } catch (Exception ignore) {
        } finally {
            IoUtil.close(is);
        }
    }
}
