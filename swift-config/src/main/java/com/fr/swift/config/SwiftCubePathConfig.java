package com.fr.swift.config;

import com.fr.config.ConfigContext;
import com.fr.config.holder.factory.Holders;
import com.fr.stable.StringUtils;
import com.fr.swift.config.base.impl.SwiftAbstractSimpleConfig;
import com.fr.swift.context.ContextUtil;

/**
 * This class created on 2018/5/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftCubePathConfig extends SwiftAbstractSimpleConfig<String> {
    private static SwiftCubePathConfig config = null;

    private static final String BASE_CUBE_PATH = getDefaultPath();

    public SwiftCubePathConfig() {
        super(Holders.<String>simple(null));
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

    private static String getDefaultPath() {
        String classPath = ContextUtil.getClassPath();
        int idx = classPath.indexOf("WEB-INF");
        if (idx != -1) {
            return classPath.substring(0, idx);
        }
        return classPath + "/../";
    }

    private static boolean isValidPath(String path) {
        return path != null && !StringUtils.isBlank(path);
    }

    @Override
    public String getNameSpace() {
        return SwiftConfigConstants.FRConfiguration.CUBE_PATH_NAMESPACE;
    }
}
