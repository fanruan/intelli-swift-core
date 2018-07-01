package com.fr.swift.config.dao;


import com.fr.third.org.hibernate.Session;
import com.fr.third.org.hibernate.criterion.Criterion;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/5/25
 */
public interface SwiftConfigDao<T> {
    /**
     * 添加或更新方法
     *
     * @param entity
     * @return
     */
    boolean saveOrUpdate(Session session, T entity) throws SQLException;

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    T select(Session session, String id) throws SQLException;

    /**
     * 自定义hql查询
     *
     * @param criterion hibernate sql
     * @return
     */
    List<T> find(Session session, Criterion... criterion);

    /**
     * 根据ID删除
     *
     * @param id
     * @return
     */
    boolean deleteById(Session session, String id) throws SQLException;
}
