package com.fr.swift.config.dao;

import java.util.Collection;
import java.util.List;

/**
 * @author anchore
 * @date 2019/12/26
 */
public interface SwiftDao<T> {
    void insert(T entity);

    void insert(Collection<T> entities);

    void insertOrUpdate(T entity);

    void insertOrUpdate(Collection<T> entities);

    void delete(T entity);

    void delete(Collection<T> entities);

    void delete(CriteriaProcessor criteriaProcessor);

    void update(T entity);

    List<?> select(CriteriaProcessor criteriaProcessor);

    List<?> selectAll();

    List<?> select(String hql, QueryProcessor queryProcessor);

    List<?> selectAll(String hql);
}