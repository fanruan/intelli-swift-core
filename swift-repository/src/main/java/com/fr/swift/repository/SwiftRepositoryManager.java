package com.fr.swift.repository;

import com.fr.swift.file.conf.SwiftFileSystemConfig;
import com.fr.swift.repository.impl.SwiftRepositoryImpl;

/**
 * @author yee
 * @date 2018/5/28
 */
public class SwiftRepositoryManager {
    private static final SwiftRepositoryManager manager = new SwiftRepositoryManager();
    private SwiftRepository defaultRepository = new SwiftRepositoryImpl(SwiftFileSystemConfig.DEFAULT);

    private SwiftRepositoryManager() {
    }

    public SwiftRepository getDefaultRepository() {
        return defaultRepository;
    }

    public static SwiftRepositoryManager getManager() {
        return manager;
    }
}
