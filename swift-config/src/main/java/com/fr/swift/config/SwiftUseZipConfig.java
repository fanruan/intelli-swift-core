package com.fr.swift.config;

import com.fr.config.ConfigContext;
import com.fr.config.holder.factory.Holders;
import com.fr.swift.config.base.impl.SwiftAbstractSimpleConfig;

/**
 * @author yee
 * @date 2018/6/15
 */
public class SwiftUseZipConfig extends SwiftAbstractSimpleConfig<Boolean> {

    private static SwiftUseZipConfig config;

    public SwiftUseZipConfig() {
        super(Holders.simple(true));
    }

    public static SwiftUseZipConfig getInstance() {
        if (config == null) {
            config = ConfigContext.getConfigInstance(SwiftUseZipConfig.class);
        }
        return config;
    }

    public boolean isUseZip() {
        return get();
    }

    public void setUseZip(boolean useZip) {
        addOrUpdate(useZip);
    }

    @Override
    public String getNameSpace() {
        return SwiftConfigConstants.FRConfiguration.ZIP_NAMESPACE;
    }
}
