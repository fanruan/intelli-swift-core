package com.fr.swift.config.oper;

/**
 * @author yee
 * @date 2018-11-28
 */
public interface ConfigTransaction {
    void commit();

    void rollback();

    void begin();
}
