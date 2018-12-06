package com.fr.swift.repository.manager;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.SwiftFileSystemConfig;
import com.fr.swift.repository.SwiftRepository;
import com.fr.swift.repository.impl.SwiftRepositoryImpl;
import com.fr.swift.service.SwiftRepositoryConfService;

/**
 * @author yee
 * @date 2018/5/28
 */
@SwiftBean(name = "swiftRepositoryManager")
public class SwiftRepositoryManager implements com.fr.swift.repository.SwiftRepositoryManager {
    private static SwiftRepository currentRepository = null;
    private SwiftRepositoryConfService service;

    public SwiftRepositoryManager() {
        service = SwiftContext.get().getBean(SwiftRepositoryConfService.class);
        service.registerListener(new SwiftRepositoryConfService.ConfChangeListener() {
            @Override
            public void change(SwiftFileSystemConfig change) {
                if (null != currentRepository) {
                    currentRepository = new SwiftRepositoryImpl(change);
                    try {
                        currentRepository.testConnection();
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

    @Override
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
                } catch (Exception e) {
                    SwiftLoggers.getLogger().warn("Create repository failed. Use default", e);
                    if (e instanceof NullPointerException) {
                        return null;
                    }
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
