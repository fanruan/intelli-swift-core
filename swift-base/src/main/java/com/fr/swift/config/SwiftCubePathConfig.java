package com.fr.swift.config;

import com.fr.base.FRContext;
import com.fr.config.ConfigContext;
import com.fr.config.DefaultConfiguration;
import com.fr.config.holder.factory.Holders;
import com.fr.config.holder.impl.MapConf;
import com.fr.stable.StringUtils;
import com.fr.swift.db.impl.SwiftDatabase.Schema;

import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2018/5/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftCubePathConfig extends DefaultConfiguration {
    private final static String NAMESPACE = "swift_cube_path";

    private static SwiftCubePathConfig config = null;

    private MapConf<Map<String, String>> paths = Holders.map(new HashMap<String, String>(), String.class, String.class);

    private static final String BASE_CUBE_PATH = String.format("%s/../", FRContext.getCurrentEnv().getPath());

    public static SwiftCubePathConfig getInstance() {
        if (null == config) {
            config = ConfigContext.getConfigInstance(SwiftCubePathConfig.class);
        }
        return config;
    }

    public void setPath(String path) {
        setPath(Schema.CUBE, path);
    }

    public String getPath() {
        return getPath(Schema.CUBE);
    }

    public void setPath(Schema swiftSchema, String path) {
        if (isValidPath(path)) {
            paths.put(swiftSchema.name, path);
        }
    }

    public String getPath(Schema swiftSchema) {
        if (paths.containsKey(swiftSchema.name)) {
            return (String) paths.get(swiftSchema.name);
        }
        return BASE_CUBE_PATH + swiftSchema.dir;
    }

    public static boolean isValidPath(String path) {
        return path != null && !StringUtils.isBlank(path);
    }

    @Override
    public String getNameSpace() {
        return NAMESPACE;
    }
}
