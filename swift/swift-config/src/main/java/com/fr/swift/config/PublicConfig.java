package com.fr.swift.config;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.CommonConnectorConfig;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.config.oper.impl.VersionConfigProperty;
import com.fr.swift.config.service.SwiftFineIOConnectorService;
import com.fr.swift.cube.io.impl.fineio.connector.CommonConnectorType;
import com.fr.swift.file.SwiftRemoteFileSystemType;
import com.fr.swift.file.system.factory.SwiftFileSystemFactory;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.repository.SwiftFileSystemConfig;
import com.fr.swift.service.SwiftRepositoryConfService;
import com.fr.swift.util.IoUtil;
import com.fr.swift.util.Strings;

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
        SwiftRepositoryConfService repositoryConfService = SwiftContext.get().getBean(SwiftRepositoryConfService.class);
        SwiftFineIOConnectorService fineIoService = SwiftContext.get().getBean(SwiftFineIOConnectorService.class);
        InputStream is = PublicConfig.class.getClassLoader().getResourceAsStream("public.conf");
        try {
            if (null != is) {
                Properties properties = new Properties();
                properties.load(is);
                try {
                    if (SwiftProperty.getProperty().isCluster()) {
                        String repoType = properties.getProperty("repo.type", VersionConfigProperty.get().getDefaultRepository());
                        if (!SwiftRemoteFileSystemType.FR.name().equals(repoType) && Strings.isNotEmpty(repoType)) {
                            SwiftFileSystemFactory factory = SwiftContext.get().getBean(repoType, SwiftFileSystemFactory.class);
                            if (null != factory) {
                                SwiftFileSystemConfig config = (SwiftFileSystemConfig) factory.loadFromProperties(properties);
                                repositoryConfService.setCurrentRepository(config);
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
                        ConfigLoader<FineIOConnectorConfig> loader = SwiftContext.get().getBean(fineIoType, ConfigLoader.class);
                        if (null != loader) {
                            config = loader.loadFromProperties(properties);
                        }
                    }
                    if (null != config) {
                        fineIoService.setCurrentConfig(config);
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
