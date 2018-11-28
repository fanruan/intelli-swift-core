package com.fr.swift.config.oper;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018-11-26
 */
public interface ConfigSession {
    void persist(Object entity);

    void merge(Object entity);

    <T> T get(Class<T> entityClass, Serializable id);

    <T> ConfigCriteria createCriteria(Class<T> entityClass);

    void delete(Object entity);

    ConfigQuery createQuery(String format);

    ConfigTransaction beginTransaction();

    void close();
}
