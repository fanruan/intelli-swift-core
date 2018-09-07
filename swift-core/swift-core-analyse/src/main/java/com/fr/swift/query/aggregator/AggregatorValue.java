package com.fr.swift.query.aggregator;

import java.io.Serializable;

/**
 * Created by pony on 2017/10/10.
 * 聚合之后的结果
 */
public interface AggregatorValue<T> extends Serializable {
    /**
     * 将聚合的结果转化为实际的值，比如去重计数的bitmap取size，平均值sum/count
     *
     * @return
     */
    double calculate();

    /**
     * 取聚合之后的对象
     *
     * @return
     */
    T calculateValue();

    Object clone();
}
