package com.fr.swift.config.conf;

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
public class SwiftPathConfig extends DefaultConfiguration {

    private final static String NAMESPACE = "cube_path";

    private static SwiftPathConfig config = null;

    private Conf<String> path = Holders.simple(StringUtils.EMPTY);

    public static SwiftPathConfig getInstance() {
        if (null == config) {
            config = ConfigContext.getConfigInstance(SwiftPathConfig.class);
        }
        return config;
    }

    public void setPath(String path) {
        this.path.set(path);
    }

    public String getPath() {
        return this.path.get();
    }

    @Override
    public String getNameSpace() {
        return NAMESPACE;
    }
}
