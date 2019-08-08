package com.fr.swift.config.command;

import com.fr.swift.config.condition.SwiftConfigCondition;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * @author yee
 * @date 2019-07-30
 */
public interface SwiftConfigCommandBus<T> {
    /**
     * 保存对象 主键冲突会抛错
     *
     * @param obj 待保存的对象
     * @return 保存成功的对象
     * @throws SQLException 主键冲突
     */
    T save(T obj) throws SQLException;

    /**
     * 保存对象集合 一个保存失败所有数据回滚
     *
     * @param objs 对象集合
     * @return 保存成功的对象
     * @throws SQLException
     */
    List<T> save(Collection<T> objs) throws SQLException;

    /**
     * saveOrUpdate
     *
     * @param obj
     * @return
     */
    T merge(T obj);

    /**
     * saveOrUpdate
     *
     * @param objs
     * @return
     */
    List<T> merge(Collection<T> objs);

    /**
     * 删除数据
     *
     * @param condition 删除条件
     * @return 已删除的对象
     */
    int delete(SwiftConfigCondition condition);

    /**
     * 执行自定义方法
     *
     * @param fn  要在事务中执行的方法
     * @param <R>
     * @return
     * @throws SQLException
     */
    <R> R transaction(SwiftConfigCommand<R> fn) throws SQLException;

    void addSaveOrUpdateListener(SaveOrUpdateListener<T> listener);

    interface SaveOrUpdateListener<T> {
        void saveOrUpdate(T entity);
    }
}
