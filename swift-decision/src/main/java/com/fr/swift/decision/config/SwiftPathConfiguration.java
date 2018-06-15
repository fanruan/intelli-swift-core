package com.fr.swift.decision.config;

import com.fr.base.FRContext;
import com.fr.config.ConfigContext;
import com.fr.config.holder.factory.Holders;
import com.fr.stable.StringUtils;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.base.impl.SwiftAbstractSimpleConfig;

/**
 * @author yee
 * @date 2018/6/15
 */
public class SwiftPathConfiguration extends SwiftAbstractSimpleConfig<String> {

    private static final String BASE_CUBE_PATH = String.format("%s/../", FRContext.getCurrentEnv().getPath());
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

    public String getNameSpace() {
        return SwiftConfigConstants.FRConfiguration.CUBE_PATH_NAMESPACE;
    }
}
