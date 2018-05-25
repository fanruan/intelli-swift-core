package com.fr.swift.config.conf.dao;

import com.fr.config.entity.Entity;

import java.util.List;

/**
 * @author yee
 * @date 2018/5/25
 */
public interface SwiftConfigDAO<T extends Entity> {
    boolean saveOrUpdate(T var1);

    T select(String var1);

    List<T> find(String var1);

    List<T> find();

    boolean deleteById(String var1);
}
