package com.fr.swift.repository.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fr.swift.SwiftContext;
import com.fr.swift.config.SwiftConfig;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.config.command.SwiftConfigCommandBus;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.query.SwiftConfigEntityQueryBus;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.SwiftRepository;
import com.fr.swift.repository.exception.RepoNotFoundException;
import com.fr.swift.repository.impl.MutableRepository;
import com.fr.swift.repository.impl.SwiftRepositoryImpl;

/**
 * @author yee
 * @date 2018/5/28
 */
public class SwiftRepositoryManager {
    private static SwiftRepository currentRepository = null;
    private SwiftConfig config;

    private SwiftRepositoryManager() {
        config = SwiftContext.get().getBean(SwiftConfig.class);
        final SwiftConfigCommandBus<SwiftConfigEntity> command = config.command(SwiftConfigEntity.class);
        command.addSaveOrUpdateListener(new SwiftConfigCommandBus.SaveOrUpdateListener<SwiftConfigEntity>() {
            @Override
            public void saveOrUpdate(SwiftConfigEntity entity) {
                if (entity.getConfigKey().contains(SwiftConfigConstants.Namespace.FINE_IO_PACKAGE.name())) {
                    if (null != currentRepository) {
                        try {
                            final FineIOConnectorConfig change = new ObjectMapper().readValue(entity.getConfigValue(), FineIOConnectorConfig.class);
                            currentRepository = new SwiftRepositoryImpl(change);
                        } catch (RepoNotFoundException e) {
                            throw e;
                        } catch (Exception e) {
                            SwiftLoggers.getLogger().warn("Create repository failed. Use default", e);
                            currentRepository = new MutableRepository();
                        }
                    }
                }
            }
        });
    }

    public static SwiftRepositoryManager getManager() {
        return SingletonHolder.manager;
    }

    public SwiftRepository currentRepo() {
        if (null == currentRepository) {
            synchronized (SwiftRepositoryManager.class) {
                FineIOConnectorConfig config = null;
                final SwiftConfigEntityQueryBus query = (SwiftConfigEntityQueryBus) this.config.query(SwiftConfigEntity.class);
                config = query.select(SwiftConfigConstants.Namespace.FINE_IO_PACKAGE, FineIOConnectorConfig.class, null);
                try {
                    currentRepository = new SwiftRepositoryImpl(config);
                } catch (RepoNotFoundException e) {
                    throw e;
                } catch (Exception e) {
                    SwiftLoggers.getLogger().warn("Create repository failed. Use default", e);
                    currentRepository = new SwiftRepositoryImpl(config);
                }
            }
        }
        return currentRepository;
    }

    private static class SingletonHolder {
        private static SwiftRepositoryManager manager = new SwiftRepositoryManager();
    }
}
