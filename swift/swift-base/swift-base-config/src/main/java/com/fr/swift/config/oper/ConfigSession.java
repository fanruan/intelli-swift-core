package com.fr.swift.config.oper;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * @author yee
 * @date 2018-11-26
 */
public interface ConfigSession {
    void persist(Object entity);

    Object merge(Object entity) throws SQLException;

    <T> T get(Class<T> entityClass, Serializable id);

    void delete(Object entity);

    ConfigQuery createQuery(String format);

    ConfigTransaction beginTransaction();

    void close();

    void save(Object convert);

    <Entity> ConfigQuery<Entity> createEntityQuery(Class<Entity> entityClass);
}
