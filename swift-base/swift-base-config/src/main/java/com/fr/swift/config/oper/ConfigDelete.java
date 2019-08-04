package com.fr.swift.config.oper;

/**
 * @author yee
 * @date 2019-08-04
 */
public interface ConfigDelete<Entity> {
    int delete();

    void where(ConfigWhere... wheres);
}
