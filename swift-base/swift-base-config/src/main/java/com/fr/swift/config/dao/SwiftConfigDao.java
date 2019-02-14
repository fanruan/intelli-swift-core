package com.fr.swift.config.dao;


import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.Order;
import com.fr.swift.converter.FindList;
import com.fr.swift.converter.ObjectConverter;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/5/25
 */
public interface SwiftConfigDao<T extends ObjectConverter> {
    /**
     * 添加或更新方法
     *
     * @param entity
     * @return
     */
    boolean saveOrUpdate(ConfigSession session, T entity) throws SQLException;

    /**
     * 同hibernate persist
     *
     * @param entity entity
     */
    void persist(ConfigSession session, T entity);

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    T select(ConfigSession session, Serializable id) throws SQLException;

    /**
     * 自定义hql查询
     *
     * @param criterion hibernate sql
     * @return
     */
    FindList<T> find(ConfigSession session, Order[] order, ConfigWhere... criterion);

    /**
     * @param session
     * @param criterion
     * @return
     */
    FindList<T> find(ConfigSession session, ConfigWhere... criterion);

    /**
     * 根据ID删除
     *
     * @param id
     * @return
     */
    boolean deleteById(ConfigSession session, Serializable id) throws SQLException;

    boolean delete(ConfigSession session, T obj) throws SQLException;
}
