package com.fr.swift.query.aggregator;

/**
 * Created by Lyon on 2018/3/30.
 */
public interface Combiner<T> {

    /**
     * 根据聚合的结果再计算
     * 合并两个值，并把合并后的值设置给current
     *
     * @param current
     * @param other
     */
    void combine(T current, T other);
}
