package com.fr.swift.config;

import com.fr.config.ConfigContext;
import com.fr.config.DefaultConfiguration;
import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.stable.StringUtils;

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

    private static final String BASE_CUBE_PATH = String.format("%s/../", FRContext.getCurrentEnv().getPath());

    private Conf<String> confPath = Holders.simple(BASE_CUBE_PATH);

    public static SwiftCubePathConfig getInstance() {
        if (null == config) {
            config = ConfigContext.getConfigInstance(SwiftCubePathConfig.class);
        }
        return config;
    }

    public void setPath(String path) {
        if (isValidPath(path)) {
            confPath.set(path);
        }
    }

    public String getPath() {
        String path = confPath.get();
        if (StringUtils.isEmpty(path)) {
            return BASE_CUBE_PATH;
        }
        return path;
    }

    public static boolean isValidPath(String path) {
        return path != null && !StringUtils.isBlank(path);
    }

    @Override
    public String getNameSpace() {
        return NAMESPACE;
    }
}
