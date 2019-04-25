package com.fr.swift.config.dao;


import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.Order;
import com.fr.swift.config.oper.Page;

import java.io.Serializable;
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
    List<T> find(ConfigSession session, Order[] order, ConfigWhere... criterion);

    /**
     * @param session
     * @param criterion
     * @return
     */
    List<T> find(ConfigSession session, ConfigWhere... criterion);

    /**
     * 根据ID删除
     *
     * @param id
     * @return
     */
    boolean deleteById(ConfigSession session, Serializable id) throws SQLException;

    boolean delete(ConfigSession session, T obj) throws SQLException;

    Page<T> findPage(ConfigSession session, int page, int size, Order[] orders, ConfigWhere... criterion);

    Page<T> findPage(ConfigSession session, int page, int size, ConfigWhere... criterion);
}
