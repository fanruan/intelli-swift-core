package com.fr.swift.decision.config;

import com.fr.config.ConfigContext;
import com.fr.config.Configuration;
import com.fr.config.holder.factory.Holders;
import com.fr.stable.StringUtils;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.context.ContextUtil;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.decision.config.base.SwiftAbstractSimpleConfig;
import com.fr.transaction.Configurations;
import com.fr.transaction.Worker;

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
        return get();
    }

    public void setPath(String path) {
        addOrUpdate(path);
    }

    @Override
    public String get() {
        String path = super.get();
        if (StringUtils.isEmpty(path)) {
            path = SwiftContext.get().getBean(SwiftCubePathService.class).getSwiftPath();
        }
        return StringUtils.isEmpty(path) ? BASE_CUBE_PATH : path;
    }

    @Override
    public boolean addOrUpdate(final String path) {
        if (isValidPath(path)) {
            Configurations.update(new Worker() {
                @Override
                public void run() {
                    SwiftCubePathConfig.super.addOrUpdate(path);
                }

                @Override
                public Class<? extends Configuration>[] targets() {
                    return new Class[]{SwiftCubePathConfig.class};
                }
            });
            return SwiftContext.get().getBean(SwiftCubePathService.class).setSwiftPath(path);
        }
        return false;
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
