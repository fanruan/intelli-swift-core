package com.fr.swift.config.service;

import com.fr.swift.config.bean.SwiftFileSystemConfig;

/**
 * @author yee
 * @date 2018/7/6
 */
public interface SwiftRepositoryConfService {
    SwiftFileSystemConfig getCurrentRepository();

    boolean setCurrentRepository(SwiftFileSystemConfig config);
}
