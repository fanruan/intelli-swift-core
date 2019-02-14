package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.storage.Connector;
import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.cube.io.impl.fineio.connector.annotation.ConnectorBuilder;
import com.fr.swift.cube.io.impl.fineio.connector.builder.FineIOConnectorBuilder;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Crasher;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018-12-20
 */
public class SwiftConnectorCreator {
    private static final SwiftConnectorCreator INSTANCE = new SwiftConnectorCreator();
    private Map<String, FineIOConnectorBuilder> builderMap = new ConcurrentHashMap<String, FineIOConnectorBuilder>();

    private SwiftConnectorCreator() {
        Map<String, Object> beans = SwiftContext.get().getBeansByAnnotations(ConnectorBuilder.class);
        for (Object value : beans.values()) {
            ConnectorBuilder builder = value.getClass().getAnnotation(ConnectorBuilder.class);
            builderMap.put(builder.value(), (FineIOConnectorBuilder) value);
        }
    }

    public static Connector create(FineIOConnectorConfig fineIOConnectorConfig) {
        String key = fineIOConnectorConfig.type();
        try {
            CommonConnectorType type = CommonConnectorType.valueOf(key);
            switch (type) {
                case LZ4:
                    Class clazz = Class.forName("com.fr.swift.cube.io.impl.fineio.connector.Lz4Connector");
                    Constructor constructor = clazz.getDeclaredConstructor(String.class);
                    constructor.setAccessible(true);
                    return (Connector) constructor.newInstance(fineIOConnectorConfig.basePath());
                case ZIP:
                    return ZipConnector.newInstance(fineIOConnectorConfig.basePath());
                case IO_STREAM:
                    return FileConnector.newInstance(fineIOConnectorConfig.basePath());
                default:
            }
        } catch (Exception ignore) {
            SwiftLoggers.getLogger().warn("{} is not a common connector. Try to find connector builder for {}", key, key);
        }
        if (INSTANCE.builderMap.containsKey(key)) {
            return INSTANCE.builderMap.get(key).setBasePath(fineIOConnectorConfig.basePath()).build();
        }
        return Crasher.crash(String.format("Cannot build connector witch type is %s", key));
    }
}
