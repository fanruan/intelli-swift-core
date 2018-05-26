package com.fr.swift.config.dao;

import com.fr.config.entity.Entity;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/5/25
 */
public interface SwiftConfigDAO<T extends Entity> {
    /**
     * 添加或更新方法
     *
     * @param entity
     * @return
     */
    boolean saveOrUpdate(T entity) throws SQLException;

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    T select(String id) throws SQLException;

    /**
     * 自定义hql查询
     *
     * @param hql hibernate sql
     * @return
     */
    List<T> find(String hql);

    /**
     * 查询所有记录
     *
     * @return
     */
    List<T> find();

    /**
     * 根据ID删除
     *
     * @param id
     * @return
     */
    boolean deleteById(String id) throws SQLException;
}
