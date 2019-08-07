package com.fr.swift.config.query;

import com.fr.swift.config.condition.SwiftConfigCondition;
import com.fr.swift.config.oper.Page;
import com.fr.swift.util.function.Function;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author yee
 * @date 2019-07-30
 */
public interface SwiftConfigQueryBus<T> {
    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    T select(Serializable id);

    /**
     * 主键查询，返回你想要的类型
     *
     * @param id
     * @param fn
     * @param <R>
     * @return
     */
    <R> R select(Serializable id, Function<T, R> fn);

    /**
     * 条件查询
     *
     * @param condition 条件
     * @return
     */
    List<T> get(SwiftConfigCondition condition);

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @param condition
     * @return
     */
    Page<T> page(int page, int size, SwiftConfigCondition condition);

    /**
     * 条件查询，返回你想要的类型
     *
     * @param condition
     * @param fn
     * @param <R>
     * @return
     */
    <R> R get(SwiftConfigCondition condition, Function<Collection<T>, R> fn);

    /**
     * 执行任意你想执行的查询
     *
     * @param fn
     * @param <R>
     * @return
     */
    <R> R get(SwiftConfigQuery<R> fn);
}
