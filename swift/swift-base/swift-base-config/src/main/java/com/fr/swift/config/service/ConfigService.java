package com.fr.swift.config.service;

import java.util.List;

/**
 * @author yee
 * @date 2018/7/6
 */
public interface ConfigService<T> {
    /**
     * 自定义条件查询
     *
     * @param criterion
     * @return
     */
    List<T> find(Object... criterion);

    /**
     * 保存
     *
     * @param obj
     * @return
     */
    boolean saveOrUpdate(T obj);
}
