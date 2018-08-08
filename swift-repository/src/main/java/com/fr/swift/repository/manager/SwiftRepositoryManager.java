package com.fr.swift.repository.manager;

import com.fr.swift.config.bean.SwiftFileSystemConfig;
import com.fr.swift.config.service.SwiftRepositoryConfService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.repository.SwiftRepository;
import com.fr.swift.repository.impl.SwiftRepositoryImpl;

/**
 * @author yee
 * @date 2018/5/28
 */
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
                SwiftFileSystemConfig config = SwiftContext.get().getBean(SwiftRepositoryConfService.class).getCurrentRepository();
                if (null != config) {
                    currentRepository = new SwiftRepositoryImpl(config);
                } else {
                    currentRepository = new SwiftRepositoryImpl(SwiftFileSystemConfig.DEFAULT);
                }
            }
        }
        return currentRepository;
    }

    private static class SingletonHolder {
        private static SwiftRepositoryManager manager = new SwiftRepositoryManager();
    }
}
