package com.fr.swift.repository.manager;

import com.fr.swift.SwiftContext;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.SwiftFileSystemConfig;
import com.fr.swift.repository.SwiftRepository;
import com.fr.swift.repository.exception.DefaultRepoNotFoundException;
import com.fr.swift.repository.impl.SwiftRepositoryImpl;
import com.fr.swift.service.SwiftRepositoryConfService;

/**
 * @author yee
 * @date 2018/5/28
 */
public class SwiftRepositoryManager {
    private static SwiftRepository currentRepository = null;
    private SwiftRepositoryConfService service;

    private SwiftRepositoryManager() {
        service = SwiftContext.get().getBean(SwiftRepositoryConfService.class);
        service.registerListener(new SwiftRepositoryConfService.ConfChangeListener() {
            @Override
            public void change(SwiftFileSystemConfig change) {
                if (null != currentRepository) {
                    try {
                        currentRepository = new SwiftRepositoryImpl(change);
                        currentRepository.testConnection();
                    } catch (DefaultRepoNotFoundException e) {
                        throw e;
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().warn("Create repository failed. Use default", e);
                        currentRepository = new SwiftRepositoryImpl();
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
                SwiftFileSystemConfig config = null;
                try {
                    config = SwiftContext.get().getBean(SwiftRepositoryConfService.class).getCurrentRepository();
                } catch (Exception e) {
                    SwiftLoggers.getLogger().warn("Cannot find repository config. Use default.");
                }
                try {
                    currentRepository = new SwiftRepositoryImpl(config);
                    currentRepository.testConnection();
                } catch (DefaultRepoNotFoundException e) {
                    throw e;
                } catch (Exception e) {
                    SwiftLoggers.getLogger().warn("Create repository failed. Use default", e);
                    currentRepository = new SwiftRepositoryImpl();
                }
            }
        }
        return currentRepository;
    }

    private static class SingletonHolder {
        private static SwiftRepositoryManager manager = new SwiftRepositoryManager();
    }
}
