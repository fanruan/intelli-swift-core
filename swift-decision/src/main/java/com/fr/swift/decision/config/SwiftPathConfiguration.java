package com.fr.swift.decision.config;

import com.fr.config.ConfigContext;
import com.fr.config.holder.factory.Holders;
import com.fr.stable.StringUtils;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.base.impl.SwiftAbstractSimpleConfig;
import com.fr.swift.context.ContextUtil;

/**
 * @author yee
 * @date 2018/6/15
 */
public class SwiftPathConfiguration extends SwiftAbstractSimpleConfig<String> {

    private static final String BASE_CUBE_PATH = getDefaultPath();
    private static SwiftPathConfiguration config = null;

    public SwiftPathConfiguration() {
        super(Holders.simple(BASE_CUBE_PATH));
    }

    public static SwiftPathConfiguration getInstance() {
        if (null == config) {
            config = ConfigContext.getConfigInstance(SwiftPathConfiguration.class);
        }
        return config;
    }

    @Override
    public boolean addOrUpdate(String obj) {
        return StringUtils.isNotEmpty(obj) && super.addOrUpdate(obj);
    }

    @Override
    public String get() {
        String path = super.get();
        return StringUtils.isNotEmpty(path) ? path : BASE_CUBE_PATH;
    }

    private static String getDefaultPath() {
        String classPath = ContextUtil.getClassPath();
        int idx = classPath.indexOf("WEB-INF");
        if (idx != -1) {
            return classPath.substring(0, idx);
        }
        return classPath + "/../";
    }

    @Override
    public String getNameSpace() {
        return SwiftConfigConstants.FRConfiguration.CUBE_PATH_NAMESPACE;
    }
}
