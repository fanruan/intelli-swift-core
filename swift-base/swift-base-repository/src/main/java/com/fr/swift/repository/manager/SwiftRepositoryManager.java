package com.fr.swift.repository.manager;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.config.service.SwiftFineIOConnectorService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.SwiftRepository;
import com.fr.swift.repository.exception.RepoNotFoundException;
import com.fr.swift.repository.impl.SwiftRepositoryImpl;

/**
 * @author yee
 * @date 2018/5/28
 */
public class SwiftRepositoryManager {
    private static SwiftRepository currentRepository = null;
    private SwiftFineIOConnectorService service;

    private SwiftRepositoryManager() {
        service = SwiftContext.get().getBean(SwiftFineIOConnectorService.class);
        service.registerListener(new SwiftFineIOConnectorService.ConfChangeListener() {
            @Override
            public void change(FineIOConnectorConfig change) {
                if (null != currentRepository) {
                    try {
                        currentRepository = new SwiftRepositoryImpl(change);
                    } catch (RepoNotFoundException e) {
                        throw e;
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().warn("Create repository failed. Use default", e);
                        currentRepository = new SwiftRepositoryImpl(change);
                    }
                }
            }
        }, SwiftFineIOConnectorService.Type.PACKAGE);
    }

    public static SwiftRepositoryManager getManager() {
        return SingletonHolder.manager;
    }

    public SwiftRepository currentRepo() {
        if (null == currentRepository) {
            synchronized (SwiftRepositoryManager.class) {
                FineIOConnectorConfig config = null;
                try {
                    config = SwiftContext.get().getBean(SwiftFineIOConnectorService.class).getCurrentConfig(SwiftFineIOConnectorService.Type.PACKAGE);
                } catch (Exception e) {
                    SwiftLoggers.getLogger().warn("Cannot find repository config. Use default.");
                }
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
