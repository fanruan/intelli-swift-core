package com.fr.swift.query.aggregator;

import java.io.Serializable;

/**
 * Created by Lyon on 2018/3/30.
 */
public interface Combiner<T> extends Serializable {

    /**
     * 根据聚合的结果再计算
     * 合并两个值，并把合并后的值设置给current
     *
     * @param current
     * @param other
     */
    void combine(T current, T other);
}
