package com.fr.swift.decision.config;

import com.fr.config.ConfigContext;
import com.fr.config.holder.factory.Holders;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.base.impl.SwiftAbstractSimpleConfig;

/**
 * @author yee
 * @date 2018/6/15
 */
public class SwiftZipConfig extends SwiftAbstractSimpleConfig<Boolean> {
    private static SwiftZipConfig config = null;

    public SwiftZipConfig() {
        super(Holders.simple(true));
    }

    public static SwiftZipConfig getInstance() {
        if (null == config) {
            config = ConfigContext.getConfigInstance(SwiftZipConfig.class);
        }
        return config;
    }

    public String getNameSpace() {
        return SwiftConfigConstants.FRConfiguration.ZIP_NAMESPACE;
    }
}
