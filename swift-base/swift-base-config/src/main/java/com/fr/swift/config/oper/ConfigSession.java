package com.fr.swift.config.oper;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * @author yee
 * @date 2018-11-26
 */
public interface ConfigSession {

    /**
     * 对应hibernate session中的merge
     *
     * @param entity
     * @return
     * @throws SQLException
     */
    Object merge(Object entity) throws SQLException;

    /**
     * 对应hibernate session中的get
     * @param entityClass
     * @param id
     * @param <T>
     * @return
     */
    <T> T get(Class<T> entityClass, Serializable id);

    /**
     * 对应hibernate session中的delete
     * @param entity
     */
    void delete(Object entity);

    /**
     * 对应hibernate session中的createQuery
     * @param format hql
     * @return
     */
    ConfigQuery createQuery(String format);

    /**
     * 对应hibernate session中的beginTransaction
     * @return
     */
    ConfigTransaction beginTransaction();

    /**
     * 对应hibernate session中的close
     */
    void close();

    /**
     * 对应hibernate session中的save
     * @param convert
     */
    void save(Object convert);

    /**
     * 创建Swift配置查询器
     * @param entityClass
     * @param <Entity>
     * @return
     */
    <Entity> ConfigQuery<Entity> createEntityQuery(Class<Entity> entityClass);
}
