package com.fr.swift.service;

import com.fr.swift.repository.PackageConnectorConfig;

/**
 * @author yee
 * @date 2018/7/6
 */
public interface SwiftRepositoryConfService {
    PackageConnectorConfig getCurrentRepository();

    boolean setCurrentRepository(PackageConnectorConfig config);

    void registerListener(ConfChangeListener listener);

    interface ConfChangeListener {
        void change(PackageConnectorConfig change);
    }
}
