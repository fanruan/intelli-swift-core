package com.fr.swift.config.service;

import com.fr.swift.config.bean.SwiftTablePathBean;

/**
 * @author yee
 * @date 2018/7/18
 */
public interface SwiftTablePathService extends ConfigService<SwiftTablePathBean> {
    boolean removePath(String table);

    Integer getTablePath(String table);

    Integer getLastPath(String table);

    SwiftTablePathBean get(String table);
}
