package com.fr.swift.service;

import com.fr.swift.repository.SwiftFileSystemConfig;

/**
 * @author yee
 * @date 2018/7/6
 */
public interface SwiftRepositoryConfService {
    SwiftFileSystemConfig getCurrentRepository();

    boolean setCurrentRepository(SwiftFileSystemConfig config);

    void registerListener(ConfChangeListener listener);

    interface ConfChangeListener {
        void change(SwiftFileSystemConfig change);
    }
}
