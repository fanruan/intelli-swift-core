package com.fr.swift.decision.config;

import com.fr.config.ConfigContext;
import com.fr.config.holder.factory.Holders;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.base.impl.SwiftAbstractSimpleConfig;
import com.fr.swift.config.service.SwiftZipService;
import com.fr.swift.context.SwiftContext;

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

    @Override
    public boolean addOrUpdate(Boolean obj) {
        super.addOrUpdate(obj);
        return SwiftContext.getInstance().getBean(SwiftZipService.class).setZip(obj);
    }

    public String getNameSpace() {
        return SwiftConfigConstants.FRConfiguration.ZIP_NAMESPACE;
    }
}
