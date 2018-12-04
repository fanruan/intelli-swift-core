package com.fr.swift.decision.config;

import com.fr.config.ConfigContext;
import com.fr.config.Configuration;
import com.fr.config.holder.factory.Holders;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.service.SwiftZipService;
import com.fr.swift.SwiftContext;
import com.fr.swift.decision.config.base.SwiftAbstractSimpleConfig;
import com.fr.transaction.Configurations;
import com.fr.transaction.Worker;

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
    public boolean addOrUpdate(final Boolean obj) {
        Configurations.update(new Worker() {
            @Override
            public void run() {
                SwiftZipConfig.super.addOrUpdate(obj);
            }

            @Override
            public Class<? extends Configuration>[] targets() {
                return new Class[]{SwiftZipConfig.class};
            }
        });

        return SwiftContext.get().getBean(SwiftZipService.class).setZip(obj);
    }

    public String getNameSpace() {
        return SwiftConfigConstants.FRConfiguration.ZIP_NAMESPACE;
    }
}
