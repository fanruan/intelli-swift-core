package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.storage.Connector;
import com.fineio.v3.connector.BaseZipPackageManager;
import com.fineio.v3.connector.PackageManager;
import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.cube.io.impl.fineio.connector.annotation.ConnectorBuilder;
import com.fr.swift.cube.io.impl.fineio.connector.builder.FineIOConnectorBuilder;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Crasher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018-12-20
 */
public class SwiftConnectorCreator {
    public static final SwiftConnectorCreator INSTANCE = new SwiftConnectorCreator();
    private Map<String, FineIOConnectorBuilder> builderMap = new ConcurrentHashMap<String, FineIOConnectorBuilder>();
    private PackageManager packageManager;

    private SwiftConnectorCreator() {
        Map<String, Object> beans = SwiftContext.get().getBeansByAnnotations(ConnectorBuilder.class);
        for (Object value : beans.values()) {
            ConnectorBuilder builder = value.getClass().getAnnotation(ConnectorBuilder.class);
            builderMap.put(builder.value(), (FineIOConnectorBuilder) value);
        }
    }

    public static Connector create(FineIOConnectorConfig fineIOConnectorConfig) {
        String key = fineIOConnectorConfig.type();
        Connector connector = null;
        try {
            CommonConnectorType type = CommonConnectorType.valueOf(key);
            switch (type) {
                case LZ4:
                    Class clazz = Class.forName("com.fr.swift.cube.io.impl.fineio.connector.Lz4Connector");
                    Constructor constructor = clazz.getDeclaredConstructor(String.class);
                    constructor.setAccessible(true);
                    connector = (Connector) constructor.newInstance(fineIOConnectorConfig.basePath());
                    break;
                case IO_STREAM:
                    connector = FileConnector.newInstance(fineIOConnectorConfig.basePath());
                    break;
                default:
            }
        } catch (Exception ignore) {
            SwiftLoggers.getLogger().warn("{} is not a common connector. Try to find connector builder for {}", key, key);
        }
        if (null == connector && INSTANCE.builderMap.containsKey(key)) {
            connector = INSTANCE.builderMap.get(key).setBasePath(fineIOConnectorConfig.basePath()).build();
        }
        if (null != connector) {
            INSTANCE.packageManager = new BaseZipPackageManager(connector) {
                @Override
                protected OutputStream output(String dir) throws IOException {
                    return null;
                }

                @Override
                protected InputStream input(String dir) throws IOException {
                    return null;
                }
            };
            return connector;
        }
        return Crasher.crash(String.format("Cannot build connector witch type is %s", key));
    }
}
