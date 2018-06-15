package com.fr.swift.config;

import com.fr.base.FRContext;
import com.fr.config.ConfigContext;
import com.fr.config.holder.factory.Holders;
import com.fr.stable.StringUtils;
import com.fr.swift.config.base.impl.SwiftAbstractSimpleConfig;

/**
 * This class created on 2018/5/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftCubePathConfig extends SwiftAbstractSimpleConfig<String> {
    private static SwiftCubePathConfig config = null;

    private static final String BASE_CUBE_PATH = String.format("%s/../", FRContext.getCurrentEnv().getPath());

    public SwiftCubePathConfig() {
        super(Holders.simple(BASE_CUBE_PATH));
    }

    public static SwiftCubePathConfig getInstance() {
        if (null == config) {
            config = ConfigContext.getConfigInstance(SwiftCubePathConfig.class);
        }
        return config;
    }

    public String getPath() {
        String path = get();
        if (StringUtils.isEmpty(path)) {
            return BASE_CUBE_PATH;
        }
        return path;
    }

    public void setPath(String path) {
        if (isValidPath(path)) {
            super.addOrUpdate(path);
        }
    }

    public static boolean isValidPath(String path) {
        return path != null && !StringUtils.isBlank(path);
    }

    @Override
    public String getNameSpace() {
        return SwiftConfigConstants.FRConfiguration.CUBE_PATH_NAMESPACE;
    }
}
