package com.fr.swift.config.service;


import com.fr.swift.config.entity.SwiftTablePathEntity;

/**
 * @author yee
 * @date 2018/7/18
 */
public interface SwiftTablePathService extends ConfigService<SwiftTablePathEntity> {
    boolean removePath(String table);

    Integer getTablePath(String table);

    Integer getLastPath(String table);

    SwiftTablePathEntity get(String table);
}
