package com.fr.swift.config.oper;

import java.util.List;

/**
 * @author yee
 * @date 2018-11-28
 */
public interface ConfigQuery<Entity> {
    /**
     * 执行查询
     *
     * @return
     */
    List<Entity> executeQuery();

    /**
     * 设置where条件
     * @param wheres
     */
    void where(ConfigWhere... wheres);

    /**
     * 设置排序
     * @param orders
     */
    void orderBy(Order... orders);


    Page<Entity> executeQuery(int page, int size);
}
