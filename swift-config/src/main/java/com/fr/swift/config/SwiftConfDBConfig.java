package com.fr.swift.config;

import com.fr.config.ConfigContext;
import com.fr.config.Configuration;
import com.fr.config.DefaultConfiguration;
import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.swift.config.bean.SwiftConfDbBean;
import com.fr.transaction.Configurations;
import com.fr.transaction.Worker;

/**
 * @author yee
 * @date 2018/6/30
 */
public class SwiftConfDBConfig extends DefaultConfiguration {

    private static SwiftConfDBConfig config;

    private Conf<SwiftConfDbBean> configHolder = Holders.obj(null, SwiftConfDbBean.class);

    public static SwiftConfDBConfig getInstance() {
        if (null == config) {
            config = ConfigContext.getConfigInstance(SwiftConfDBConfig.class);
        }
        return config;
    }

    public SwiftConfDbBean getConfig() {
        return configHolder.get();
    }

    public void setConfig(final SwiftConfDbBean config) {
        Configurations.update(new Worker() {
            @Override
            public void run() {
                configHolder.set(config);
            }

            @Override
            public Class<? extends Configuration>[] targets() {
                return new Class[]{SwiftConfDBConfig.class};
            }
        });

    }

    @Override
    public String getNameSpace() {
        return SwiftConfigConstants.FRConfiguration.SWIFT_DB_CONF_NAMESPACE;
    }
}
