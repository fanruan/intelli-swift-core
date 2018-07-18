package com.fr.swift.config.service;

import com.fr.swift.config.entity.SwiftTablePathEntity;

/**
 * @author yee
 * @date 2018/7/18
 */
public interface SwiftTablePathService extends ConfigService<SwiftTablePathEntity> {
    boolean removePath(String table);

    String getTablePath(String table);

    String getLastPath(String table);

    SwiftTablePathEntity get(String table);
}
