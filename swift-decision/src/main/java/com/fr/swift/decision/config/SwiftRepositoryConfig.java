package com.fr.swift.decision.config;

import com.fr.config.ConfigContext;
import com.fr.config.Configuration;
import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.base.impl.SwiftAbstractObjectMapConfig;
import com.fr.swift.config.bean.RepositoryConfBean;
import com.fr.swift.file.system.SwiftFileSystemType;
import com.fr.transaction.Configurations;
import com.fr.transaction.Worker;

/**
 * @author yee
 * @date 2018/6/15
 */
public class SwiftRepositoryConfig extends SwiftAbstractObjectMapConfig<RepositoryConfBean> {

    private static SwiftRepositoryConfig config;
    private Conf<String> type = Holders.simple(SwiftFileSystemType.FR.name());

    public SwiftRepositoryConfig() {
        super(RepositoryConfBean.class);
    }

    public static SwiftRepositoryConfig getInstance() {
        if (null == config) {
            config = ConfigContext.getConfigInstance(SwiftRepositoryConfig.class);
        }
        return config;
    }

    public RepositoryConfBean getCurrentRepository() {
        return get(type.get());
    }

    public void setCurrentRepository(final SwiftFileSystemType type, RepositoryConfBean bean) {
        Configurations.update(new Worker() {
            @Override
            public void run() {
                SwiftRepositoryConfig.this.type.set(type.name());
            }

            @Override
            public Class<? extends Configuration>[] targets() {
                return new Class[]{SwiftRepositoryConfig.class};
            }
        });
        if (type != SwiftFileSystemType.FR) {
            addOrUpdate(type.name(), bean);
        }
    }

    @Override
    public String getNameSpace() {
        return SwiftConfigConstants.FRConfiguration.REPOSITORY_CONF_NAMESPACE;
    }
}
