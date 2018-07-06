package com.fr.swift.repository;

import com.fr.swift.config.bean.SwiftFileSystemConfig;
import com.fr.swift.config.service.SwiftRepositoryConfService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.repository.impl.SwiftRepositoryImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/5/28
 */
public class SwiftRepositoryManager {
    private static final SwiftRepositoryManager manager = new SwiftRepositoryManager();

    private Map<SwiftFileSystemConfig, SwiftRepository> fileSystemMap = new ConcurrentHashMap<SwiftFileSystemConfig, SwiftRepository>();

    private SwiftRepository defaultRepository = new SwiftRepositoryImpl(SwiftFileSystemConfig.DEFAULT);

    private SwiftRepositoryManager() {
    }

    public SwiftRepository getCurrentRepository() {
        SwiftFileSystemConfig config = SwiftContext.getInstance().getBean(SwiftRepositoryConfService.class).getCurrentRepository();
        if (null != config) {
            SwiftRepository repository = fileSystemMap.get(config);
            if (null == repository) {
                repository = new SwiftRepositoryImpl(config);
                fileSystemMap.put(config, repository);
            }
            return repository;
        }
        return defaultRepository;
    }

    public static SwiftRepositoryManager getManager() {
        return manager;
    }
}
